package com.dmzj.akitajyx.autoscrollviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoScrollViewPager autoScrollViewPager;
    private LinearLayout layoutDot;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> strings = new ArrayList<>();
        strings.add("我是标题一");
        strings.add("我是标题二");
        strings.add("我是标题三");
        strings.add("我是标题四");
        List<String> img = new ArrayList<>();
        img.add("http://pic32.nipic.com/20130829/12906030_124355855000_2.png");
        img.add("http://www.people.com.cn/mediafile/pic/20151116/83/16613124312696240379.jpg");
        img.add("http://n1.itc.cn/img8/wb/recom/2015/11/24/144837717308445247.jpeg");
        img.add("http://f.hiphotos.baidu.com/image/pic/item/2cf5e0fe9925bc3161866ee25ddf8db1ca1370f4.jpg");
        autoScrollViewPager = (AutoScrollViewPager) findViewById(R.id.auto);
        textView = (TextView) findViewById(R.id.tv);
        layoutDot = (LinearLayout) findViewById(R.id.ll_dot);
        autoScrollViewPager.setLooping(true);
        autoScrollViewPager.setTitle(strings,textView);
        autoScrollViewPager.setImgUrl(img);
        autoScrollViewPager.init(4,layoutDot);
        autoScrollViewPager.setItemClickListener(new AutoScrollViewPager.OnViewItemClickListener() {
            @Override
            public void onItemClick() {
                Toast.makeText(getBaseContext(),"点击了",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        autoScrollViewPager.startScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        autoScrollViewPager.stopscoll();
    }
}
