package com.example.onosystems;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
       implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    public ArrayList<Delivery> deliveryInfo = new ArrayList<>();
    public List<Map<String, String>> list = new ArrayList<>();
    public ListView listView;
    public ToggleButton toggle0, toggle1, toggle2, toggle3;
    public SimpleDateFormat sdf = new SimpleDateFormat("MM/dd kk:mm"); //日付フォーマット
    public int deliveredStatus;
    public int receivableStatus;

    public int toolBarLayout;
    public Class detailActivity;

    public ActionBarDrawerToggle toggle;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        setUserOptions();
        toolbarView();
        getDeliveries();
        reloadDeliveries();
        findDeliveries();
    }

    public void setUserOptions() { }

    public void getDeliveries() {
        //本来はサーバからデータ受け取る
        try {
            JSONArray json = new JSONArray("[{\"name\":\"001\", \"time\":\"1546239600\", \"slip_number\":\"1111\", \"address\":\"1001\", \"ship_from\":\"ヤマト\", \"delivered_status\":\"1\", \"receivable_status\":\"0\"}," +
                    "{\"name\":\"002\", \"time\":\"1545980400\", \"slip_number\":\"1112\", \"address\":\"1002\", \"ship_from\":\"佐川\", \"delivered_status\":\"0\", \"receivable_status\":\"1\"}," +
                    "{\"name\":\"003\", \"time\":\"1545951600\", \"slip_number\":\"1113\", \"address\":\"1003\", \"ship_from\":\"カンガルー\", \"delivered_status\":\"1\", \"receivable_status\":\"2\"}]");

            for (int i = 0; i < json.length(); i++) {
                JSONObject deliveryData = json.getJSONObject(i);
                deliveryInfo.add(new Delivery(deliveryData.getLong("slip_number"),
                                              deliveryData.getString("name"),
                                              deliveryData.getString("address"),
                                              deliveryData.getString("ship_from"),
                                              deliveryData.getInt("time"),
                                              deliveryData.getInt("delivered_status"),
                                              deliveryData.getInt("receivable_status"),
                                              Boolean.TRUE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void reloadDeliveries() {
        list = new ArrayList<>(); //初期化

        for (int i = 0; i < deliveryInfo.size(); i++) {
            Map<String, String> item = new HashMap<>();
            Date date = new Date(deliveryInfo.get(i).getTime() * 1000L);
            String statusName =  String.valueOf(getResources().getIdentifier("receivable_image" + deliveryInfo.get(i).getReceivable_status(),"drawable",this.getPackageName()));

            if(deliveryInfo.get(i).getVisible()) {
                item.put("name", deliveryInfo.get(i).getName());
                item.put("time", String.valueOf(sdf.format(date)));
                item.put("slipNumber", String.valueOf(deliveryInfo.get(i).getSlipNumber()));
                item.put("address", deliveryInfo.get(i).getAddress());
                item.put("ship_from", deliveryInfo.get(i).getShip_from());
                item.put("delivered_status", String.valueOf(deliveryInfo.get(i).getDelivered_status()));
                item.put("receivable_status", String.valueOf(deliveryInfo.get(i).getReceivable_status()));
                // item.put("unixTime", String.valueOf(deliveryInfo.get(i).getTime())); //受け渡し用
                item.put("image", statusName);
                list.add(item);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                list, R.layout.list_layout,
                new String[]{"name", "time", "slipNumber", "address", "image", "ship_from"}, // どの項目を
                new int[]{R.id.addressText, R.id.timeText, R.id.slipNumberText, R.id.deliveryAddressText, R.id.image, R.id.shipFrom} // どのidの項目に入れるか
        );

        listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this); // リストの項目が選択されたときのイベントを追加
    }

    //toolbarのアイテム表示
    public void toolbarView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu_camera);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(toolBarLayout, menu);

        toggle0 = menu.findItem(R.id.toggle0).getActionView().findViewById(R.id.toggle_layout_switch0);
        toggle1 = menu.findItem(R.id.toggle1).getActionView().findViewById(R.id.toggle_layout_switch1);
        toggle2 = menu.findItem(R.id.toggle2).getActionView().findViewById(R.id.toggle_layout_switch2);
        toggle3 = menu.findItem(R.id.toggleReceivable).getActionView().findViewById(R.id.receivableSelect);
        toggle0.setChecked(true);
        toggle1.setChecked(true);
        toggle2.setChecked(true);
        toggle3.setChecked(true);
        toggle0.setOnCheckedChangeListener(this);
        toggle1.setOnCheckedChangeListener(this);
        toggle2.setOnCheckedChangeListener(this);
        toggle3.setOnCheckedChangeListener(this);

        return true;
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int buttonId = buttonView.getId();

        switch(buttonId) {
            case R.id.toggle_layout_switch0:
                toggleVisibleFromReceivable(0, isChecked);
                break;
            case R.id.toggle_layout_switch1:
                toggleVisibleFromReceivable(1, isChecked);
                break;
            case R.id.toggle_layout_switch2:
                toggleVisibleFromReceivable(2, isChecked);
                break;
            case R.id.receivableSelect:
                deliveredSelect(isChecked);
                break;
        }
    }

    // リスト項目が押されたときのイベント
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplication(), detailActivity);  // 遷移先指定
        //intent.putExtra("itemInfo", listView.getItemAtPosition(position).toString());
        startActivity(intent);// 詳細画面に遷移
    }

    //バッグボタンが押されたときのイベント
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //どこにも遷移しない
        }
    }

    // 検索関係
    public void findDeliveries() {
        SearchView search = findViewById(R.id.searchView);
        search.setOnQueryTextListener(this);
        listView.setTextFilterEnabled(true); // インクリメンタルサーチをおこなうかどうか
        search.setQueryHint("検索文字を入力して下さい"); // 何も入力されてないときのテキスト
    }
    public boolean onQueryTextSubmit(String query){
        return false; // summitButtonを実装していないので，falseを返すだけのやつ
    }
    public boolean onQueryTextChange(String queryText){
        SimpleAdapter filterList = (SimpleAdapter) listView.getAdapter();

        if (TextUtils.isEmpty(queryText)) {
            filterList.getFilter().filter(null);
        } else {
            filterList.getFilter().filter(queryText.toString());
        }

        return true;
    }

    //商品ステータスの切り替え
    public void toggleVisibleFromReceivable(int number, boolean isChecked) {
        for (int i = 0; i < deliveryInfo.size(); i++) {
            deliveredStatus = deliveryInfo.get(i).getDelivered_status();
            receivableStatus = deliveryInfo.get(i).getReceivable_status();

            if(receivableStatus == number) {
                if (isChecked) {
                    if(!(!toggle3.isChecked() && deliveredStatus== 1)) {
                        deliveryInfo.get(i).setVisible(true);
                    }
                } else {
                    deliveryInfo.get(i).setVisible(false);
                }
            }
        }

        reloadDeliveries();
    }
    public void deliveredSelect(boolean isChecked) {
        Boolean check[] = {toggle0.isChecked(), toggle1.isChecked(), toggle2.isChecked()};

        for (int i = 0; i < deliveryInfo.size(); i++) {
            deliveredStatus = deliveryInfo.get(i).getDelivered_status();
            receivableStatus = deliveryInfo.get(i).getReceivable_status();

            if (deliveredStatus == 1) {
                if (isChecked && check[receivableStatus]) {
                    deliveryInfo.get(i).setVisible(true);
                } else {
                    deliveryInfo.get(i).setVisible(false);
                }
            }
        }

        reloadDeliveries();
    }

}

class Delivery {
    long slipNumber;
    String name;
    String address;
    String ship_from;
    int time;
    int delivered_status;
    int receivable_status;
    boolean visible;
    int UNDELIVERED = 0;
    int DELIVERD = 1;
    int UNSELECTED = 0;
    int UNRECEIVABLE = 1;
    int RECEIVABLE =2;

    public long getSlipNumber() { return this.slipNumber; }

    public String getName() { return this.name; }

    public String getAddress() { return this.address; }

    public String getShip_from() { return ship_from; }

    public int getTime() { return this.time; }

    public int getDelivered_status() { return delivered_status; }

    public int getReceivable_status() { return receivable_status; }

    public boolean getVisible() { return visible; }

    public void setVisible(boolean visible) { this.visible = visible; }

    public Delivery(long slipNumber, String name, String address, String ship_from, int time,
                    int delivered_status, int receivable_status, boolean visible) {
        this.slipNumber = slipNumber;
        this.name = name;
        this.address = address;
        this.ship_from = ship_from;
        this.time = time;
        this.delivered_status = delivered_status;
        this.receivable_status = receivable_status;
        this.visible = visible;
    }
}