package com.example.dropdownmenu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * <pre>
 *     author : Clement
 *     time   : 2017/05/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DropDownMenuView extends RelativeLayout {

    //默认插值器
    private Interpolator mInterpolator = new DecelerateInterpolator();
    //顶部的view
    private View viewTop;
    //筛选条件的view
    private View viewMenu;
    //遮盖层的view
    private View viewMask;
    private Context mContext;

    //遮盖层默认颜色
    private int mMaskViewColor = 0x60434444;
    //动画时间
    private int mDurationTime = 300;
    //判断当前状态
    private boolean mIsOpen = false;

    public DropDownMenuView(Context context) {
        this(context,null);
    }

    public DropDownMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DropDownMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //初始化属性
        initAttr(attrs);
        initView();
    }

    private void initView(){
        //添加一个遮盖层
        addMaskView();
        //检查是否满足要求
        checkLayout();
    }

    /**
     * 初始化属性
     */
    private void initAttr(@Nullable AttributeSet attrs){
        TypedArray array = mContext.obtainStyledAttributes(attrs,R.styleable.DropDownMenuView);
        mMaskViewColor = array.getColor(R.styleable.DropDownMenuView_mask_view_color, mMaskViewColor);
        mDurationTime = array.getInteger(R.styleable.DropDownMenuView_duration,mDurationTime);
        array.recycle();
    }

    /**
     * 添加一层遮盖层(只有在展开情况下，才会显示遮盖层)
     */
    private void addMaskView(){
        if(viewMask!=null){
            return;
        }
        viewMask = new View(mContext);
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        viewMask.setLayoutParams(params);
        //灰黑色透明背景
        viewMask.setBackgroundColor(mMaskViewColor);
        //添加view
        addView(viewMask);
        //默认是隐藏的
        viewMask.setVisibility(GONE);
        viewMask.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                //收缩遮盖层
                close();
            }
        });
    }

    /**
     * 检查是否满足条件（需要view绘制完成才能检查）
     */
    private void checkLayout(){
        post(new Runnable() {
            @Override public void run() {
                //检查父布局是否满足要求
                checkParentLayout();
                //检查子view的数量
                checkChildCount();
                viewTop = getChildAt(1);
                viewMenu = getChildAt(2);
                //默认是隐藏的
                viewMenu.setVisibility(GONE);
            }
        });
    }

    /**
     * 检查DropDownMenu所在的容器布局，如果是LinearLayout,抛出错误
     */
    private void checkParentLayout(){
        ViewGroup viewGroup = (ViewGroup) this.getParent();
        if(!(viewGroup instanceof FrameLayout)){
            throw new RuntimeException("ParentView must is FrameLayout ");
        }
    }

    /**
     * 检查子view的数量，最多只有三个（有一个是遮盖层）
     */
    private void checkChildCount(){
        if(this.getChildCount()!=3){
            throw new RuntimeException("Only two child view support!");
        }
    }

    /**
     * 展开动画
     */
    private void openAnimation(){
        //设置展开的基准位置,从顶部开始展开(默认是中心位置展开收缩)
        viewMenu.setPivotY(0);
        viewMenu.setVisibility(View.VISIBLE);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewMenu,"scaleY",0f,1f);
        scaleY.setDuration(mDurationTime);
        scaleY.setInterpolator(mInterpolator);
        scaleY.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                mIsOpen = true;
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
        //渐变显示maskView
        viewMask.setVisibility(View.VISIBLE);
        changeAlpha(viewMask,0f,1f);
    }

    /**
     * 收缩动画
     */
    private void closeAnimation(){
        viewMenu.setPivotY(0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewMenu,"scaleY",1f,0f);
        scaleY.setDuration(mDurationTime);
        scaleY.setInterpolator(mInterpolator);
        scaleY.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
                viewMenu.setVisibility(View.GONE);
                if(viewMask!=null){
                    viewMask.setVisibility(View.GONE);
                }
                mIsOpen = false;
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
        //渐变隐藏maskView
        changeAlpha(viewMask,1f,0f);
    }

    /**
     * 实现背景渐变
     * @param view
     * @param startStatus
     * @param endStatus
     */
    private void changeAlpha(@NonNull View view, float startStatus,float endStatus){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view,"alpha",startStatus,endStatus);
        alpha.setDuration(mDurationTime);
        alpha.start();
    }

    /***************************************外部调用方法********************************************/

    /**
     * 展开
     */
    public void open(){
        if(viewMenu==null){
            return;
        }
        openAnimation();
    }

    /**
     * 收缩
     */
    public void close(){
        if(viewMenu==null){
            return;
        }
        closeAnimation();
    }

    /**
     * 判断当前是处于展开状态还是隐藏状态
     * @return
     */
    public boolean isOpen() {
        return mIsOpen;
    }

    /**
     * 更改动画的插值器
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }
}
