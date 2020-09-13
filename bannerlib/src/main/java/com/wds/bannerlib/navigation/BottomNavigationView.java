package com.wds.bannerlib.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TabHost;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.wds.bannerlib.R;

import java.util.ArrayList;
import java.util.List;


public class BottomNavigationView extends ConstraintLayout {
    //水平的dp
    private static final int HORIZONTAL_MARGIN_DP = 32;
    //竖直的dp
    private static final int VERTICAL_MARGIN_DP = 12;
    //list集合
    private List<Integer> mList = new ArrayList<>();
    //集合
    private List<String> mTitle = new ArrayList<>();
    private int mMarginLeft;
    private int mMarginTop;
    private int mMarginBottom;
    private int mMarginRight;
    private int mTextSize;

    private int mDrawableMargin;
    private int mDrawableIconWidth;
    private int mDrawableIconHeight;

    private int mDeliverLineWidth;//线的宽和高
    private int mDeliverLineColor;//线的颜色
    private ColorStateList mTextColorStateList;
    //适配器
    private NavigationAdapter simpleNavigationAdapter;
    //监听
    private OnTabSelectedListener mTabSelectedListener;
    private boolean mHasDeliverLine; // 是否显示分割线
    private int mCurrentPosition = 0;
    private Paint mLinePaint;
    public BottomNavigationView(Context context) {
        super(context);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValue(attrs);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
    }

    private void initValue(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.BottomNavigationView);

        mMarginLeft = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationLeftMargin,dip2px(HORIZONTAL_MARGIN_DP));
        mMarginRight = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationRightMargin,dip2px(HORIZONTAL_MARGIN_DP));
        mMarginTop = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationTopMargin,dip2px(VERTICAL_MARGIN_DP));
        mMarginBottom = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationBottomMargin, dip2px(VERTICAL_MARGIN_DP));
        mDrawableIconWidth = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationDrawableWidth, 0);
        mDrawableIconHeight = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationDrawableHeight, 0);

        mDrawableMargin = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationDrawableMargin,0);
        mTextSize = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationTextSize,0);

        mTextColorStateList = array.getColorStateList(R.styleable.BottomNavigationView_bottomNavigationTextColor);
        mHasDeliverLine = array.getBoolean(R.styleable.BottomNavigationView_bottomNavigationDeliverLine, true);
        mDeliverLineWidth = array.getDimensionPixelSize(R.styleable.BottomNavigationView_bottomNavigationDeliverLineWidth, dip2px( 1));
        mDeliverLineColor = array.getColor(R.styleable.BottomNavigationView_bottomNavigationDeliverLineColor, Color.GRAY);

        array.recycle();
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mDeliverLineColor);
        mLinePaint.setStrokeWidth(mDeliverLineWidth);
        setWillNotDraw(!mHasDeliverLine);
}
    public void setTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
        this.mTabSelectedListener = tabSelectedListener;
        //如果适配器不等于空
        if (simpleNavigationAdapter != null) {
            //获取每个View
            View tabView = simpleNavigationAdapter.getHolderByPosition(mCurrentPosition).mItemView;
            //传入View 和  控件的tag (presenter)
            mTabSelectedListener.onTabSelect(tabView, (Integer) tabView.getTag());

        }
    }

    public BottomNavigationView addItem(int drawableId, String title){
        mList.add(drawableId);
        mTitle.add(title);
        return this;
    }
    public void apply(){
        //把drawableId  和 title 应用到布局中
        if (mList.size()>0){
            ArrayList<TabBean> tabBeans = new ArrayList<>();
            for (int i = 0; i < mList.size(); i++) {
                tabBeans.add(new TabBean(mList.get(i),mTitle.get(i)));
            }
            apply(new SimpleNavigationAdapter(tabBeans));
        }
    }
    public void apply(NavigationAdapter<? extends TabHolder> adapter){
        simpleNavigationAdapter=adapter;
            initView();
        }
    private void initView() {
        removeAllViews();

        int minTabWidth = Integer.MAX_VALUE; // 所有tab 中最小的那一个的宽度
        int maxTabHeight = 0; // 所有tab 中最高哪一个的高度
        int maxTabHeightIndex = 0; // 最高tab 的index
        //循环获取适配器中的item
        for (int i = 0; i < simpleNavigationAdapter.getCount(); i++) {
            TabHolder holder = simpleNavigationAdapter.createHolder(this, i);
            addView(holder.mItemView);
            simpleNavigationAdapter.bindData(holder,i);

            //计算每一个tab 的宽和高
            holder.mItemView.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED),MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
            if (holder.mItemView.getMeasuredWidth()<minTabWidth){
                minTabWidth=holder.mItemView.getMeasuredWidth();
            }
            if (holder.mItemView.getMeasuredHeight()>maxTabHeight){
                maxTabHeight=holder.mItemView.getMeasuredHeight();
                maxTabHeightIndex=i;
            }

        }

        //添加约束条件
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        int previousId = 0; // 用于记录上一个 id
        View view;
        int ids [] = new int[simpleNavigationAdapter.getCount()];

        // 在使用链的时候，如果是水平的链，那么就只需要把链上的所有控件的垂直方向上的约束添加上即可
        //先把最高的那一个tab 固定好
        view = simpleNavigationAdapter.getHolderByPosition(maxTabHeightIndex).mItemView;
        constraintSet.connect(view.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
        constraintSet.connect(view.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
        ids[maxTabHeightIndex] =view.getId();
        previousId=view.getId();

        //最高的左边
        if (maxTabHeightIndex>0){
            for (int i=maxTabHeightIndex-1;i>=0;i--){
                view = simpleNavigationAdapter.getHolderByPosition(i).mItemView;
                constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, previousId, ConstraintSet.BOTTOM);
                previousId=view.getId();
                ids[i]=view.getId();
            }
        }
        if (maxTabHeightIndex<simpleNavigationAdapter.getCount()-1){
            for (int i = maxTabHeightIndex+1; i <simpleNavigationAdapter.getCount() ; i++) {
                view = simpleNavigationAdapter.getHolderByPosition(i).mItemView;
                constraintSet.connect(view.getId(),ConstraintSet.BOTTOM,previousId,ConstraintSet.BOTTOM);
                previousId=view.getId();
                ids[i]=view.getId();
            }
        }


        // 第一个参数： 你这个链的左端需要连接到的控件的Id
        // 第二个参数： 你这个链的左端需要连接到第一个参数指定的控件的那一边
        // 第三个参数： 你这个链的右端需要连接大的控件的Id
        // 第四个参数： 你这个链的右端需要连接到第三个参数指定的控件的那一边
        // 第五个参数： 你这个链上多有控件的Id 的一个数组
        // 第六个参数： 权重
        // 第七个参数： 链的模式
        constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID,ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,ids,null,ConstraintSet.CHAIN_SPREAD_INSIDE);
        constraintSet.applyTo(this);
        //获取最大的数值   当padding
        int padding = Math.max(mMarginBottom, mMarginTop);
        //通过最小tab的宽度/2     获取左边距离，右边距离的最小
        int paddingOffset = Math.min(minTabWidth / 2, Math.min(mMarginLeft, mMarginRight));
        //如果有分割线  就让padding+线的宽度， 没有就padding
        int paddingTop = mHasDeliverLine ? padding + mDeliverLineWidth : padding;
        //对适配器监听
        for (int i = 0; i < simpleNavigationAdapter.getCount(); i++) {
            simpleNavigationAdapter.getHolderByPosition(i).mItemView.setPadding(paddingOffset,paddingTop,paddingOffset,padding);
        }
         setPadding(mMarginLeft-paddingOffset,0,mMarginRight-paddingOffset,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHasDeliverLine){
            canvas.drawLine(0,0,getWidth(),0,mLinePaint);
        }
    }

    //创建适配器
    public static class SimpleNavigationAdapter implements NavigationAdapter <SimpleNavigationAdapter.SimpleTabHolder>{
        private int mInd=1000;
        private ArrayList<TabBean> list;
        private ArrayList<SimpleTabHolder> simpleTabHolders=new ArrayList<>();
        private boolean isFirst=true;
        private CheckBox mPreCheckedTab;
        public SimpleNavigationAdapter(ArrayList<TabBean> list) {
            this.list = list;
        }

        @Override
        public SimpleTabHolder createHolder(ViewGroup parent, int position) {
            final BottomNavigationView bottomNavigationView=(BottomNavigationView)parent;
            CheckBox checkBox = new CheckBox(parent.getContext());
            checkBox.setTag(position);//得到tag
            checkBox.setId(mInd++);//id
            checkBox.setButtonDrawable(null);//取消掉小方格
            checkBox.setGravity(Gravity.CENTER);
            checkBox.setTextColor(bottomNavigationView.mTextColorStateList);
            int mTextSize = bottomNavigationView.mTextSize;
            if (mTextSize>0){
                checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
            }
            //监听
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //判断监听不为空
                    if (bottomNavigationView.mTabSelectedListener != null) {
                        //如果為true
                        if (isChecked) {
                            //判断当前的view 是否是上一个view  不是
                            if (mPreCheckedTab != buttonView) {
                                //设置点击成功
                                bottomNavigationView.mTabSelectedListener.onTabSelect(buttonView, (Integer) buttonView.getTag());
                                //如果上一个tab 不为空
                                if (mPreCheckedTab!=null){
                                    //取消
                                    bottomNavigationView.mTabSelectedListener.onTabUnSelect(mPreCheckedTab, (Integer) mPreCheckedTab.getTag());

                                    //以为再次点击后上一次没有设置为false 又因为点击了当前  没法将再次执行下面代码
                                    //所有定义一个空的变量并将mPreCheckedTab赋值
                                    CheckBox temp = mPreCheckedTab;
                                    //将当前的赋值给mPreCheckedTab
                                    mPreCheckedTab= (CheckBox) buttonView;
                                    //temp 是否选中设为false
                                    temp.setChecked(false);
                                    //当不上多次点击就不必要向下执行
                                    return;
                                }
                                //当当前的view 复值给mPreCheckedTab 后 取消的没法执行mPreCheckedTab= (CheckBox) buttonView;代码
                                mPreCheckedTab= (CheckBox) buttonView;
                            }

                        } else {
                            if(mPreCheckedTab==buttonView){
                                bottomNavigationView.mTabSelectedListener.onTabReSelected(buttonView, (Integer) buttonView.getTag());
                                mPreCheckedTab.setChecked(true);
                            }
                        }
                    }
                }
            });


            SimpleTabHolder simpleTabHolder = new SimpleTabHolder(checkBox);
            simpleTabHolders.add(simpleTabHolder);
            return simpleTabHolder;
        }

        @Override
        public void bindData(SimpleTabHolder view, final int position) {
            //获取icon
            Drawable drawable = view.mItemView.getResources().getDrawable(list.get(position).getDrawable());
            //获取当前的 父布局
            BottomNavigationView navigationView = (BottomNavigationView) view.mItemView.getParent();
            //进行判断 如果icon 的宽和高都大于0
            if (navigationView.mDrawableIconWidth > 0 && navigationView.mDrawableIconHeight > 0) {
                //就设置icon 的边距
                drawable.setBounds(0, 0, navigationView.mDrawableIconWidth, navigationView.mDrawableIconHeight);
                view.mItemView.setCompoundDrawables(null, drawable, null, null);
            } else {
                view.mItemView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }

            view.mItemView.setText(list.get(position).getTitle());

            if (navigationView.mCurrentPosition==position){
                view.mItemView.setChecked(true);
                if (navigationView.mTabSelectedListener==null){
                    mPreCheckedTab=view.mItemView;
                }
            }
        }

        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public SimpleTabHolder getHolderByPosition(int position) {
            return simpleTabHolders.size()==0?null:simpleTabHolders.get(position);
        }

        public class SimpleTabHolder extends TabHolder<CheckBox> {
            SimpleTabHolder(CheckBox view) {
                super(view);
            }
        }
    }

    private static abstract class TabHolder<T extends View>{
         protected T  mItemView;
        public TabHolder(T t){
         this.mItemView=t;
        }
    }
    //创建适配器的接口
    public interface NavigationAdapter <TH extends TabHolder>{
        TH createHolder(ViewGroup parent, int position);
        void bindData(TH view, int position);
        int getCount();
        TH getHolderByPosition(int position);
    }

    //创建bean类
    public static class TabBean{
        private int drawable;
        private String title;

        public TabBean(int drawable, String title) {
            this.drawable = drawable;
            this.title = title;
        }

        public int getDrawable() {
            return drawable;
        }

        public void setDrawable(int drawable) {
            this.drawable = drawable;
        }

        public String  getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    //dp  转px
    public  int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //创建监听接口
    public interface OnTabSelectedListener {

        void onTabSelect(View tab, int position);

        void onTabUnSelect(View tab, int position);

        void onTabReSelected(View tab, int position);
    }

}
