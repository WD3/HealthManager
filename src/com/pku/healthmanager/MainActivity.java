package com.pku.healthmanager;

import java.util.Calendar;
import java.util.Date;

import com.pku.calendar.CalendarGridView;
import com.pku.calendar.CalendarGridViewAdapter;
import com.pku.calendar.CalendarUtil;
import com.pku.calendar.CalendarView;
import com.pku.calendar.NumberHelper;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends TabActivity implements OnClickListener,OnTouchListener{
	private TabHost tabHost;
	private Context context;
	private TextView tv_month;
	private TextView tv_day;
	private LinearLayout linearLayout;
	private RelativeLayout home;
	private LinearLayout relativeLayout;
	private CalendarView calendarView;
	
	
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



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ʵ���ޱ�����������ϵͳ�Դ�������������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		// ��ȡTabHost����
		tabHost = getTabHost();
		
		// �½�һ��newTabSpec,���ñ�ǩ��ͼ��(setIndicator),��������(setContent)
		tabHost.addTab(tabHost.newTabSpec("home").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_home)).setContent(R.id.home));
		tabHost.addTab(tabHost.newTabSpec("health").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_health)).setContent(R.id.health));
		tabHost.addTab(tabHost.newTabSpec("user").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_user)).setContent(R.id.user));
		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_setting)).setContent(R.id.setting));
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));}
		// ���õ�ǰ��ʵ��һ����ǩ
		tabHost.setCurrentTab(0); // 0Ϊ��ǩID		
		// ��ǩ�л�������setOnTabChangedListener
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId.equals("home")) {
					
				} else if (tabId.equals("health")) {
					 
				} else if (tabId.equals("user")) {
					
				} else if (tabId.equals("setting")) {
					
				}
			}
		});		
		tv_month = (TextView)findViewById(R.id.month);
		tv_month.setOnClickListener(this);
		tv_day = (TextView)findViewById(R.id.day);
		tv_day.setOnClickListener(this);
		linearLayout = (LinearLayout)findViewById(R.id.calendar_day);
		relativeLayout = (LinearLayout)findViewById(R.id.calendar_month);		
		home = (RelativeLayout)findViewById(R.id.home);
		linearLayout.setVisibility(View.VISIBLE);
		relativeLayout.setVisibility(View.INVISIBLE);
//		calendarView = new CalendarView(this,home);
		initView();
        updateStartDateForMonth();

        generateContetView(mCalendarMainLayout);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

        slideLeftIn.setAnimationListener(animationListener);
        slideLeftOut.setAnimationListener(animationListener);
        slideRightIn.setAnimationListener(animationListener);
        slideRightOut.setAnimationListener(animationListener);

        mGesture = new GestureDetector(this, new GestureListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.month:
			linearLayout.setVisibility(View.INVISIBLE);
			relativeLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.day:
			linearLayout.setVisibility(View.VISIBLE);
			relativeLayout.setVisibility(View.INVISIBLE);
			break;
		}
	}
	/**
     * ���ڳ�ʼ���ؼ�
     */
    private void initView() {
        mDayMessage = (TextView) findViewById(R.id.day_message);
        mCalendarMainLayout = (RelativeLayout) findViewById(R.id.calendar_main);
        mPreMonthImg = (ImageView) findViewById(R.id.left_img);
        mNextMonthImg = (ImageView) findViewById(R.id.right_img);
        mPreMonthImg.setOnClickListener(onPreMonthClickListener);

        mNextMonthImg.setOnClickListener(onNextMonthClickListener);
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
        viewFlipper = new ViewFlipper(this);
        viewFlipper.setId(CAL_LAYOUT_ID);
        calStartDate = getCalendarStartDate();
        CreateGirdView();
        RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        layout.addView(viewFlipper, params_cal);

        LinearLayout br = new LinearLayout(this);
        RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
                LayoutParams.FILL_PARENT, 1);
        params_br.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
        br.setBackgroundColor(getResources().getColor(R.color.calendar_background));
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

        firstGridView = new CalendarGridView(context);
        firstCalendar.add(Calendar.MONTH, -1);
        firstGridAdapter = new CalendarGridViewAdapter(this, firstCalendar);
        firstGridView.setAdapter(firstGridAdapter);// ���ò˵�Adapter
        firstGridView.setId(CAL_LAYOUT_ID);

        currentGridView = new CalendarGridView(context);
        currentGridAdapter = new CalendarGridViewAdapter(this, currentCalendar);
        currentGridView.setAdapter(currentGridAdapter);// ���ò˵�Adapter
        currentGridView.setId(CAL_LAYOUT_ID);

        lastGridView = new CalendarGridView(context);
        lastCalendar.add(Calendar.MONTH, 1);
        lastGridAdapter = new CalendarGridViewAdapter(this, lastCalendar);
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
                .get(Calendar.MONTH) + 1);
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
                    mDayMessage.setText(new CalendarUtil(calSelected).toString());
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

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
