package com.example.muhanxi.shopdemo.shop;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muhanxi.shopdemo.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopActivity extends Activity {

    @BindView(R.id.third_recyclerview)
    RecyclerView thirdRecyclerview;
    @BindView(R.id.third_allselect)
    TextView thirdAllselect;
    @BindView(R.id.third_totalprice)
    TextView thirdTotalprice;
    @BindView(R.id.third_totalnum)
    TextView thirdTotalnum;
    @BindView(R.id.third_submit)
    TextView thirdSubmit;
    @BindView(R.id.third_pay_linear)
    LinearLayout thirdPayLinear;
    private List<ShopBean.OrderDataBean.CartlistBean> mAllOrderList = new ArrayList<>();
    private ShopAdapter adapter;
    private LinearLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);

        getData();

        // 1 为选中  2 选中
        thirdAllselect.setTag(1);

        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        adapter = new ShopAdapter(this);

        thirdRecyclerview.setLayoutManager(manager);
        thirdRecyclerview.setAdapter(adapter);


        setFirst(mAllOrderList);
        adapter.add(mAllOrderList);


        adapter.setCheckBoxListener(new ShopAdapter.CheckBoxListener() {
            @Override
            public void check(int position, int count, boolean check,List<ShopBean.OrderDataBean.CartlistBean> list) {


                sum(list);
            }
        });

        adapter.setCustomViewListener(new ShopAdapter.CustomViewListener() {
            @Override
            public void click(int count,List<ShopBean.OrderDataBean.CartlistBean> list) {
                sum(list);
            }
        });

        adapter.setDelListener(new ShopAdapter.DelListener() {
            @Override
            public void del(int position,List<ShopBean.OrderDataBean.CartlistBean> list) {
                sum(list);
            }
        });


    }


    /**
     * 是否需要 显示商家
     *
     * @param mAllOrderList
     */
    private void setFirst(List<ShopBean.OrderDataBean.CartlistBean> mAllOrderList){

        if(mAllOrderList.size()>0){
            mAllOrderList.get(0).setIsFirst(1);

            for(int i=1;i<mAllOrderList.size();i++){

                if(mAllOrderList.get(i).getShopId() == mAllOrderList.get(i-1).getShopId()){
                    mAllOrderList.get(i).setIsFirst(2);
                }else {
                    mAllOrderList.get(i).setIsFirst(1);
                }
            }



        }

    }


    float price = 0;
    int count;

    /**
     * 计算总价
     * @param mAllOrderList
     */
    private void sum(List<ShopBean.OrderDataBean.CartlistBean> mAllOrderList) {
        price = 0;
        count = 0;

        boolean allCheck = true ;
        for (ShopBean.OrderDataBean.CartlistBean bean : mAllOrderList) {
            if (bean.isCheck()) {
                //得到总价
                price += bean.getPrice() * bean.getCount();
                //得到商品个数
                count += bean.getCount();
            }else {
                // 只要有一个商品未选中，全选按钮 应该设置成 为选中
                allCheck = false;
            }
        }

        thirdTotalprice.setText("总价: " + price);
        thirdTotalnum.setText("共" + count + "件商品");

        if(allCheck){
            thirdAllselect.setTag(2);
            thirdAllselect.setBackgroundResource(R.drawable.shopcart_selected);
        }else {
            thirdAllselect.setTag(1);
            thirdAllselect.setBackgroundResource(R.drawable.shopcart_unselected);
        }

    }


    public void getData() {
        try {
            //模拟网络请求
            InputStream inputStream = getAssets().open("shop.json");
            String data = convertStreamToString(inputStream);
            Gson gson = new Gson();
            ShopBean shopBean = gson.fromJson(data, ShopBean.class);


            for (int i = 0; i < shopBean.getOrderData().size(); i++) {
                int length = shopBean.getOrderData().get(i).getCartlist().size();
                for (int j = 0; j < length; j++) {
                    mAllOrderList.add(shopBean.getOrderData().get(i).getCartlist().get(j));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    boolean select = false ;
    @OnClick(R.id.third_allselect)
    public void onClick() {
        //全选按钮 点击事件

        int tag = (Integer) thirdAllselect.getTag() ;


        if(tag ==1){
            thirdAllselect.setTag(2);
            select = true;

        } else {
            thirdAllselect.setTag(1);
            select = false;
        }
        for (ShopBean.OrderDataBean.CartlistBean bean : mAllOrderList) {
            bean.setCheck(select);
            bean.setShopCheck(select);
        }
        adapter.notifyDataSetChanged();

        sum(adapter.getList());





    }
}
