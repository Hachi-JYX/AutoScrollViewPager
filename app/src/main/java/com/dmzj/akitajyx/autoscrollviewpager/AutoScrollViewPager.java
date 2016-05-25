package com.dmzj.akitajyx.autoscrollviewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义轮播条控件
 */
public class AutoScrollViewPager extends ViewPager {
    private int pageCount = 2;//页面最少为2页
    private int currentIndex = 0;
    private List<String> imgUrl;//图片链接
    private List<String> titles;//标题链接
    private List<ImageView> dots = new ArrayList<>();//指示器的集合
    private TextView textView;//标题控件
    private boolean isLooping = false;//是否支持无限轮播
    private int delayTime = 3000;//轮播的间隔时间
    private  OnItemClickListener onItemClickListener;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                int index = getCurrentItem();
                index++;
                setCurrentItem(index);

                Message msg2 = Message.obtain();
                msg2.what=1;
                mHandler.sendMessageDelayed(msg2,delayTime);
            }
        }
    };

    public interface OnItemClickListener{
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTitles(List<String> titles,TextView textView) {
        this.titles = titles;
        this.textView = textView;
        this.textView.setText(titles.get(0));
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    public AutoScrollViewPager(Context context) {
        super(context);
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int pageNum, LinearLayout layout){
        this.pageCount = pageNum;//获得总页数
        //动态生成指示器的元素
        for (int i = 0; i < pageCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(R.drawable.dot_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 8;
            layout.addView(imageView,params);
            dots.add(imageView);
        }
        dots.get(0).setSelected(true);//设置第一个点默认选中
        PagerAdapter adapter = new MyPagerAdapter();
        this.setOnPageChangeListener(new PageScrollListener());
        this.setAdapter(adapter);
    }

    public void startScroll(){
        Message msg = Message.obtain();
        msg.what=1;
        mHandler.sendMessageDelayed(msg,delayTime);
    }

    public void stopScroll(){
        //清除所有的消息
        mHandler.removeCallbacksAndMessages(null);
    }

    class PageScrollListener implements OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            dots.get(currentIndex % pageCount).setSelected(false);
            currentIndex = position;
            dots.get(currentIndex % pageCount).setSelected(true);

            if (titles!=null && textView!=null){
                textView.setText(titles.get(currentIndex % pageCount));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class PageOntouchEvent implements OnTouchListener{

        int downx=0;
        long downTime=0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    stopScroll();
                    downx = (int) getX();
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    int upx = (int) getX();
                    if (upx==downx && System.currentTimeMillis() - downTime<300){
                        if (onItemClickListener!=null){
                            onItemClickListener.onItemClick(currentIndex % pageCount);
                        }
                    }
                    startScroll();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    startScroll();
                    break;
            }
            return true;
        }
    }
    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            if (isLooping){
                return Integer.MAX_VALUE;
            }else {
                return imgUrl.size();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewPager.LayoutParams params = new LayoutParams();
            params.height = LayoutParams.MATCH_PARENT;
            params.width = LayoutParams.MATCH_PARENT;
            if (imgUrl!=null){
                Glide.with(getContext()).load(imgUrl.get(position % pageCount)).into(imageView);
            }
            container.addView(imageView,params);
            imageView.setOnTouchListener(new PageOntouchEvent());
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
