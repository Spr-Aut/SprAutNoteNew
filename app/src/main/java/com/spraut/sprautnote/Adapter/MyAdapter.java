package com.spraut.sprautnote.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.transition.Transition;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.spraut.sprautnote.AddEdit.EditActivity;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.R;
import com.spraut.sprautnote.widget.WidgetSingleObject22;

import java.util.Calendar;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Note> mBeanList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private NoteDbOpenHelper mNoteDbOpenHelper;
    private int viewType;
    public static int TYPE_LINEAR_LAYOUT = 0;
    public static int TYPE_GRID_LAYOUT = 1;

    public MyAdapter(Context mContext,List<Note> mBeanList){
        this.mBeanList=mBeanList;
        this.mContext=mContext;
        mLayoutInflater=LayoutInflater.from(mContext);
        mNoteDbOpenHelper=new NoteDbOpenHelper(mContext);
    }

    public void setViewType(int viewType){
        this.viewType=viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==TYPE_LINEAR_LAYOUT){
            View view=mLayoutInflater.inflate(R.layout.item_main,parent,false);
            MyViewHolder myViewHolder=new MyViewHolder(view);
            return myViewHolder;
        }else if (viewType==TYPE_GRID_LAYOUT){
            View view=mLayoutInflater.inflate(R.layout.item_main,parent,false);
            MyViewHolder myViewHolder=new MyViewHolder(view);
            return myViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder==null){
            return;
        }
        if (holder instanceof MyViewHolder){
            bindMyViewHolder((MyViewHolder) holder,position);
        }
    }


    private void bindMyViewHolder(MyViewHolder holder,int position){

        //解决显示错乱问题
        holder.mTvObject.setVisibility(View.VISIBLE);
        holder.mTvEvent.setVisibility(View.VISIBLE);
        holder.mTvDate.setVisibility(View.VISIBLE);
        holder.mTvRemain.setVisibility(View.VISIBLE);
        holder.mIbRemove.setVisibility(View.VISIBLE);
        holder.mVgItem.setVisibility(View.VISIBLE);
        holder.mCardView.setVisibility(View.VISIBLE);


        Note note=mBeanList.get(position);

        //month可能会报错 notice
        Calendar mcalendar = Calendar.getInstance();     //  获取当前时间    —   年、月、日
        int year = mcalendar.get(Calendar.YEAR);         //  得到当前年
        int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  得到当前月
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  得到当前日

        int remain=day_minus(year,month,day,note.getYear_end(),note.getMonth_end(),note.getDay_end());

        holder.mTvObject.setText(note.getObject());
        holder.mTvEvent.setText(note.getEvent());
        if (remain>0){
            holder.mTvRemain.setText(remain+"天后");
        }else if (remain==0){
            holder.mTvRemain.setText("今天");
        }else if (remain<0){
            holder.mTvRemain.setText(-remain+"天前");
        }
        holder.mTvDate.setText(note.getYear_end()+"年"+note.getMonth_end()+"月"+note.getDay_end()+"日");
        if(remain<=3&&remain>=0){
            holder.mTvRemain.setTextColor(Color.argb(255,255,0,0));
        }else if (remain<0){
            holder.mTvRemain.setTextColor(Color.argb(255,82,136,245));
        }else if (remain>3){
            holder.mTvRemain.setTextColor(Color.argb(255,117,117,117));
        }
        //#5288F5
        //点击整个Item
        holder.mVgItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //共享元素
                Pair pairTvDate=new Pair<>(holder.mTvDate,"tvTime");
                Pair pairTvObject=new Pair<>(holder.mTvObject,"editObject");
                Pair pairTvEvent=new Pair<>(holder.mTvEvent,"editEvent");
                Pair pairItem=new Pair<>(holder.mVgItem,"Item");
                Pair pairCard=new Pair<>(holder.mCardView,"Card");

                Intent intent=new Intent(mContext, EditActivity.class);
                intent.putExtra("note",note);
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,pairCard);
                mContext.startActivity(intent,activityOptions.toBundle());




            }
        });




        //长按整个Item
        holder.mVgItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // 弹出弹窗
                Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                View dialogView = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);

                //dialog背景
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                WindowManager.LayoutParams layoutParams=dialog.getWindow().getAttributes();
                layoutParams.dimAmount=0.0f;
                dialog.getWindow().setAttributes(layoutParams);

                TextView tvDelete = dialogView.findViewById(R.id.tv_delete);
                TextView tvEdit = dialogView.findViewById(R.id.tv_edit);

                //点击删除
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int row = mNoteDbOpenHelper.deleteFromDbById(note.getId()+"");
                        if (row > 0) {
                            removeData(position);
                        }
                        dialog.dismiss();

                        // 延迟发送更新广播，目的是回到桌面后更新小部件视图
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(800);
                                } catch (InterruptedException e) {
//                            e.printStackTrace();
                                } finally {
                                    // 发送更新广播
                                    Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE", null, mContext, WidgetSingleObject22.class);
                                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{0});
                                    mContext.sendBroadcast(intent);
                                }
                            }
                        }.start();
                    }
                });

                //点击编辑
                tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EditActivity.class);//已修改 notice
                        intent.putExtra("note", note);
                        mContext.startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(dialogView);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                return true;
            }
        });

        //点击删除按钮
        holder.mIbRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int row=mNoteDbOpenHelper.deleteFromDbById(note.getId()+"");
                if (row>0){
                    removeData(position);
                }

                // 延迟发送更新广播，目的是回到桌面后更新小部件视图
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        } finally {
                            // 发送更新广播
                            Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE", null, mContext, WidgetSingleObject22.class);
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{0});
                            mContext.sendBroadcast(intent);
                        }
                    }
                }.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void refreshData(List<Note> notes){
        this.mBeanList=notes;
        notifyDataSetChanged();
    }

    //删除Item
    private void removeData(int position) {
        mBeanList.remove(position);

        //显示remove动画
        notifyItemRemoved(position);
        //删除后刷新被删的Item之后的Item，不要用notifyItemRangeRemoved，不然后面的Item会闪一下
        notifyItemRangeChanged(position,mBeanList.size()-position);

    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTvObject;
        TextView mTvEvent;
        TextView mTvRemain;
        TextView mTvDate;
        ImageButton mIbRemove;
        ViewGroup mVgItem;
        CardView mCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvObject=itemView.findViewById(R.id.tv_item_object);
            this.mTvEvent=itemView.findViewById(R.id.tv_item_event);
            this.mTvRemain=itemView.findViewById(R.id.tv_item_remain);
            this.mTvDate=itemView.findViewById(R.id.tv_item_date);
            this.mIbRemove=itemView.findViewById(R.id.remove_item);
            this.mVgItem=itemView.findViewById(R.id.LinearLayout_item);
            this.mCardView=itemView.findViewById(R.id.card_item_main);
        }
    }

    public int day_minus(int year_start, int month_start, int day_start, int year_end, int month_end, int day_end) {
        int y2, m2, d2;
        int y1, m1, d1;
        /*用于判断日期是否大于3月（2月是判断闰年的标识），还用于纪录到3月的间隔月数*/
        m1 = (month_start + 9) % 12;
        /*如果是1月和2月，则不包括当前年（因为是计算到0年3月1日的天数）*/
        y1 = year_start - m1 / 10;
    /*365*y1 是不算闰年多出那一天的天数， y1/4 - y1/100 + y1/400  是加所有闰年多出的那一天
    (day_start - 1) 用于计算当前日到1日的间隔天数。*/
        d1 = 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 + (m1 * 306 + 5) / 10 + (day_start - 1);
        /*(m2*306 + 5)/10 用于计算到当前月到3月1日间的天数 306=365-31-28（1月和2月），5是全年中不是31天月份的个数*/
        m2 = (month_end + 9) % 12;
        y2 = year_end - m2 / 10;
        d2 = 365 * y2 + y2 / 4 - y2 / 100 + y2 / 400 + (m2 * 306 + 5) / 10 + (day_end - 1);
        return (d2 - d1);
    }


}