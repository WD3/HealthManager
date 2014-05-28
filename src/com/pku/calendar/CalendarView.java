package com.pku.calendar;

import java.util.Calendar;
import java.util.Date;

import com.pku.healthmanager.R;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class CalendarView implements OnTouchListener,OnClickListener{
	private Activity activity;
    /**
     * ��������ID
     */
    private static final int CAL_LAYOUT_ID = 55;
    //�ж�������
    private static final int SWIPE_MIN_DISTANCE = 120;

    private static final int SWIPE_MAX_OFF_PATH = 250;

    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    //����
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    GestureDetector mGesture = null;

    /**
     * ���찴ť
     */
    private Button mTodayBtn;

    /**
     * ��һ���°�ť
     */
    private ImageView mPreMonthImg;

    /**
     * ��һ���°�ť
     */
    private ImageView mNextMonthImg;

    /**
     * ������ʾ���������
     */
    private TextView mDayMessage;

    /**
     * ����װ��������View
     */
    private RelativeLayout mCalendarMainLayout;

    /**
     * ��һ����View
     */
    private GridView firstGridView;

    /**
     * ��ǰ��View
     */
    private GridView currentGridView;

    /**
     * ��һ����View
     */
    private GridView lastGridView;

    /**
     * ��ǰ��ʾ������
     */
    private Calendar calStartDate = Calendar.getInstance();

    /**
     * ѡ�������
     */
    private Calendar calSelected = Calendar.getInstance();

    /**
     * ����
     */
    private Calendar calToday = Calendar.getInstance();

    /**
     * ��ǰ����չʾ������Դ
     */
    private CalendarGridViewAdapter currentGridAdapter;

    /**
     * Ԥװ����һ����չʾ������Դ
     */
    private CalendarGridViewAdapter firstGridAdapter;

    /**
     * Ԥװ����һ����չʾ������Դ
     */
    private CalendarGridViewAdapter lastGridAdapter;
    //
    /**
     * ��ǰ��ͼ��
     */
    private int mMonthViewCurrentMonth = 0;

    /**
     * ��ǰ��ͼ��
     */
    private int mMonthViewCurrentYear = 0;

    /**
     * ��ʼ��
     */
    private int iFirstDayOfWeek = Calendar.MONDAY;
    
	public CalendarView(Activity activity,RelativeLayout relativeLayout){
		this.activity = activity;
		this.mCalendarMainLayout = relativeLayout;
		display();
	}
	public boolean onTouch(View v, MotionEvent event) {
        return mGesture.onTouchEvent(event);
    }    
	public void display(){
		initView();
        updateStartDateForMonth();

        generateContetView(mCalendarMainLayout);
        slideLeftIn = AnimationUtils.loadAnimation(activity, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(activity, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(activity, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(activity, R.anim.slide_right_out);

        slideLeftIn.setAnimationListener(animationListener);
        slideLeftOut.setAnimationListener(animationListener);
        slideRightIn.setAnimationListener(animationListener);
        slideRightOut.setAnimationListener(animationListener);

        mGesture = new GestureDetector(activity, new GestureListener());
	}
	/**
     * ���ڳ�ʼ���ؼ�
     */
    private void initView() {
        mDayMessage = (TextView)mCalendarMainLayout.findViewById(R.id.day_message);
        mPreMonthImg = (ImageView)mCalendarMainLayout.findViewById(R.id.left_arrow);
        mNextMonthImg = (ImageView)mCalendarMainLayout.findViewById(R.id.right_arrow);
        mCalendarMainLayout = (RelativeLayout)mCalendarMainLayout.findViewById(R.id.calendar_main);
        mPreMonthImg.setOnClickListener(this);
        mNextMonthImg.setOnClickListener(this);
    }
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.left_arrow:
			viewFlipper.setInAnimation(slideRightIn);
			viewFlipper.setOutAnimation(slideRightOut);
			viewFlipper.showPrevious();
			setPrevViewItem();			
			break;
		case R.id.right_arrow:
			viewFlipper.setInAnimation(slideLeftIn);
            viewFlipper.setOutAnimation(slideLeftOut);
            viewFlipper.showNext();
            setNextViewItem();
			break;
		}
	}	
    /**
     * ���ڼ��ص���ǰ�����ڵ��¼�
     */
    private View.OnClickListener onTodayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            calStartDate = Calendar.getInstance();
            updateStartDateForMonth();
            generateContetView(mCalendarMainLayout);
        }
    };

    /**
     * ���ڼ�����һ�������ڵ��¼�
     */
    private View.OnClickListener onPreMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewFlipper.setInAnimation(slideRightIn);
            viewFlipper.setOutAnimation(slideRightOut);
            viewFlipper.showPrevious();
            setPrevViewItem();
        }
    };

    /**
     * ���ڼ�����һ�������ڵ��¼�
     */
    private View.OnClickListener onNextMonthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            viewFlipper.setInAnimation(slideLeftIn);
            viewFlipper.setOutAnimation(slideLeftOut);
            viewFlipper.showNext();
            setNextViewItem();
        }
    };

    /**
     * ��Ҫ�������ɷ�ǰչʾ������View
     *
     * @param layout ��Ҫ����ȥ���صĲ���
     */
    private void generateContetView(RelativeLayout layout) {
        // ����һ����ֱ�����Բ��֣��������ݣ�
        viewFlipper = new ViewFlipper(activity);
        viewFlipper.setId(CAL_LAYOUT_ID);
        calStartDate = getCalendarStartDate();
        CreateGirdView();
        RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        layout.addView(viewFlipper, params_cal);

        LinearLayout br = new LinearLayout(activity);
        RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 1);
        params_br.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
        br.setBackgroundColor(activity.getResources().getColor(R.color.calendar_background));
        layout.addView(br, params_br);
    }

    /**
     * ���ڴ�����ǰ��Ҫ����չʾ��View
     */
    private void CreateGirdView() {

        Calendar firstCalendar = Calendar.getInstance(); // ��ʱ
        Calendar currentCalendar = Calendar.getInstance(); // ��ʱ
        Calendar lastCalendar = Calendar.getInstance(); // ��ʱ
        firstCalendar.setTime(calStartDate.getTime());
        currentCalendar.setTime(calStartDate.getTime());
        lastCalendar.setTime(calStartDate.getTime());

        firstGridView = new CalendarGridView(activity);
        firstCalendar.add(Calendar.MONTH, -1);
        firstGridAdapter = new CalendarGridViewAdapter(activity, firstCalendar);
        firstGridView.setAdapter(firstGridAdapter);// ���ò˵�Adapter
        firstGridView.setId(CAL_LAYOUT_ID);

        currentGridView = new CalendarGridView(activity);
        currentGridAdapter = new CalendarGridViewAdapter(activity, currentCalendar);
        currentGridView.setAdapter(currentGridAdapter);// ���ò˵�Adapter
        currentGridView.setId(CAL_LAYOUT_ID);

        lastGridView = new CalendarGridView(activity);
        lastCalendar.add(Calendar.MONTH, 1);
        lastGridAdapter = new CalendarGridViewAdapter(activity, lastCalendar);
        lastGridView.setAdapter(lastGridAdapter);// ���ò˵�Adapter
        lastGridView.setId(CAL_LAYOUT_ID);

        currentGridView.setOnTouchListener(this);
        firstGridView.setOnTouchListener(this);
        lastGridView.setOnTouchListener(this);

        if (viewFlipper.getChildCount() != 0) {
            viewFlipper.removeAllViews();
        }

        viewFlipper.addView(currentGridView);
        viewFlipper.addView(lastGridView);
        viewFlipper.addView(firstGridView);

        String s = calStartDate.get(Calendar.YEAR)
                + "-"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1)+"-"+NumberHelper.LeftPad_Tow_Zero(calStartDate.get(Calendar.DATE));
        mDayMessage.setText(s);
    }

    /**
     * ��һ����
     */
    private void setPrevViewItem() {
        mMonthViewCurrentMonth--;// ��ǰѡ����--
        // �����ǰ��Ϊ�����Ļ���ʾ��һ��
        if (mMonthViewCurrentMonth == -1) {
            mMonthViewCurrentMonth = 11;
            mMonthViewCurrentYear--;
        }
        calStartDate.set(Calendar.DAY_OF_MONTH, 1); // ������Ϊ����1��
        calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth); // ������
        calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear); // ������

    }

    /**
     * ��һ����
     */
    private void setNextViewItem() {
        mMonthViewCurrentMonth++;
        if (mMonthViewCurrentMonth == 12) {
            mMonthViewCurrentMonth = 0;
            mMonthViewCurrentYear++;
        }
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear);

    }
    /**
     * ���ݸı�����ڸ�������
     * ��������ؼ���
     */
    private void updateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // ���óɵ��µ�һ��
        mMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// �õ���ǰ������ʾ����
        mMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// �õ���ǰ������ʾ����

        String s = calStartDate.get(Calendar.YEAR)
                + "-"
                + NumberHelper.LeftPad_Tow_Zero(calStartDate
                .get(Calendar.MONTH) + 1);
        mDayMessage.setText(s);
        // ����һ��2 ��������1 ���ʣ������
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

    }

    /**
     * ���ڻ�ȡ��ǰ��ʾ�·ݵ�ʱ��
     *
     * @return ��ǰ��ʾ�·ݵ�ʱ��
     */
    private Calendar getCalendarStartDate() {
        calToday.setTimeInMillis(System.currentTimeMillis());
        calToday.setFirstDayOfWeek(iFirstDayOfWeek);

        if (calSelected.getTimeInMillis() == 0) {
            calStartDate.setTimeInMillis(System.currentTimeMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        } else {
            calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        }

        return calStartDate;
    }

    AnimationListener animationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //��������ɺ����
            CreateGirdView();
        }
    };

    class GestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                    viewFlipper.showNext();
                    setNextViewItem();
                    return true;

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    viewFlipper.showPrevious();
                    setPrevViewItem();
                    return true;

                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //�õ���ǰѡ�е��ǵڼ�����Ԫ��
            int pos = currentGridView.pointToPosition((int) e.getX(), (int) e.getY());
            LinearLayout txtDay = (LinearLayout) currentGridView.findViewById(pos + 5000);
            if (txtDay != null) {
                if (txtDay.getTag() != null) {
                    Date date = (Date) txtDay.getTag();
                    calSelected.setTime(date);
                    mDayMessage.setText(new CalendarLunar().getLunarCalendar(date));
                    currentGridAdapter.setSelectedDate(calSelected);
                    currentGridAdapter.notifyDataSetChanged();
                    firstGridAdapter.setSelectedDate(calSelected);
                    firstGridAdapter.notifyDataSetChanged();

                    lastGridAdapter.setSelectedDate(calSelected);
                    lastGridAdapter.notifyDataSetChanged();
                }
            }

            Log.i("TEST", "onSingleTapUp -  pos=" + pos);

            return false;
        }
    }

}
