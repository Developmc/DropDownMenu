package com.example.dropdownmenu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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
public class DropDownMenu extends RelativeLayout {

    //顶部的view
    private View viewTop;
    //筛选条件的view
    private View viewMenu;
    //遮盖层的view
    private View viewMask;

    private Context mContext;
    //判断当前状态
    private boolean mIsOpen = false;

    public DropDownMenu(Context context) {
        this(context,null);
    }

    public DropDownMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DropDownMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView(){
        //添加一个遮盖层
        addMaskView();
        //检查是否满足要求
        checkLayout();
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
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewMenu,"scaleY",0f,1f);
        scaleY.setDuration(300);
        viewMenu.setVisibility(View.VISIBLE);
        scaleY.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                if(viewMask!=null){
                    viewMask.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
    }

    /**
     * 收缩动画
     */
    private void closeAnimation(){
        viewMenu.setPivotY(0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewMenu,"scaleY",1f,0f);
        scaleY.setDuration(300);
        scaleY.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
                viewMenu.setVisibility(View.GONE);
                if(viewMask!=null){
                    viewMask.setVisibility(View.GONE);
                }
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
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
        viewMask.setBackgroundColor(0x60434444);
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

    /***************************************外部调用方法********************************************/

    /**
     * 展开
     */
    public void open(){
        if(viewMenu==null){
            return;
        }
        openAnimation();
        mIsOpen = true;
    }

    /**
     * 收缩
     */
    public void close(){
        if(viewMenu==null){
            return;
        }
        closeAnimation();
        mIsOpen = false;
    }

    /**
     * 判断当前是处于展开状态还是隐藏状态
     * @return
     */
    public boolean isOpen() {
        return mIsOpen;
    }

}
