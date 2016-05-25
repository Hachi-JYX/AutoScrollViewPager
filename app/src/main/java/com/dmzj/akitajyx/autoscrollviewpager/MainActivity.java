package com.dmzj.akitajyx.autoscrollviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoScrollViewPager autoScrollViewPager;
    private LinearLayout layoutDot;
    private TextView textView;
    private List<String> strings;
    private List<String> img;
    private ListView listView;
    private List<String> items = new ArrayList<>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        listView = (ListView) findViewById(R.id.lv);
        View headView = View.inflate(getBaseContext(),R.layout.head_layout,null);
        autoScrollViewPager = (AutoScrollViewPager) headView.findViewById(R.id.auto);
        textView = (TextView) headView.findViewById(R.id.tv);
        layoutDot = (LinearLayout) headView.findViewById(R.id.ll_dot);
        autoScrollViewPager.setLooping(true);
        autoScrollViewPager.setTitles(strings,textView);
        autoScrollViewPager.setImgUrl(img);
        autoScrollViewPager.setDelayTime(2000);
        autoScrollViewPager.init(4,layoutDot);
        autoScrollViewPager.setOnItemClickListener(new AutoScrollViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int postion) {
                Toast.makeText(getBaseContext(),"点击了"+postion,Toast.LENGTH_SHORT).show();
            }
        });
        listView.addHeaderView(headView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

    }

    private void initData() {
        strings = new ArrayList<>();
        strings.add("我是标题一");
        strings.add("我是标题二");
        strings.add("我是标题三");
        strings.add("我是标题四");
        img = new ArrayList<>();
        img.add("http://pic32.nipic.com/20130829/12906030_124355855000_2.png");
        img.add("http://www.people.com.cn/mediafile/pic/20151116/83/16613124312696240379.jpg");
        img.add("http://n1.itc.cn/img8/wb/recom/2015/11/24/144837717308445247.jpeg");
        img.add("http://f.hiphotos.baidu.com/image/pic/item/2cf5e0fe9925bc3161866ee25ddf8db1ca1370f4.jpg");
        for (int i = 0; i < 15; i++) {
            items.add("我是第"+i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        autoScrollViewPager.startScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        autoScrollViewPager.stopScroll();
    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getBaseContext(),R.layout.itme_layout,null);
            TextView textView = (TextView) view.findViewById(R.id.tv_item);
            textView.setText(items.get(position));
            return view;
        }
    }
}
