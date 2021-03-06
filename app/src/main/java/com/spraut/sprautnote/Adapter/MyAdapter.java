package com.spraut.sprautnote.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.transition.Transition;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.spraut.sprautnote.AddEdit.AddActivity;
import com.spraut.sprautnote.AddEdit.EditActivity;
import com.spraut.sprautnote.DataBase.Note;
import com.spraut.sprautnote.DataBase.NoteDbOpenHelper;
import com.spraut.sprautnote.R;
import com.spraut.sprautnote.widget.SendBroadcast;
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

        //????????????????????????
        holder.mTvObject.setVisibility(View.VISIBLE);
        holder.mTvEvent.setVisibility(View.VISIBLE);
        holder.mTvDate.setVisibility(View.VISIBLE);
        holder.mTvRemain.setVisibility(View.VISIBLE);
        holder.mIbRemove.setVisibility(View.VISIBLE);
        holder.mVgItem.setVisibility(View.VISIBLE);
        holder.mCardView.setVisibility(View.VISIBLE);
        holder.mCardViewSlide.setVisibility(View.VISIBLE);


        Note note=mBeanList.get(position);

        //month??????????????? notice
        Calendar mcalendar = Calendar.getInstance();     //  ??????????????????    ???   ???????????????
        int year = mcalendar.get(Calendar.YEAR);         //  ???????????????
        int month = mcalendar.get(Calendar.MONTH) + 1 ;       //  ???????????????
        final int day = mcalendar.get(Calendar.DAY_OF_MONTH);  //  ???????????????

        int remain=day_minus(year,month,day,note.getYear_end(),note.getMonth_end(),note.getDay_end());

        holder.mTvObject.setText(note.getObject());
        holder.mTvEvent.setText(note.getEvent());
        if (remain>0){
            holder.mTvRemain.setText(remain+"??????");
        }else if (remain==0){
            holder.mTvRemain.setText("??????");
        }else if (remain<0){
            holder.mTvRemain.setText(-remain+"??????");
        }
        holder.mTvDate.setText(note.getYear_end()+"???"+note.getMonth_end()+"???"+note.getDay_end()+"???");
        if(remain<=3&&remain>=0){
            holder.mTvRemain.setTextColor(Color.argb(255,255,0,0));
        }else if (remain<0){
            holder.mTvRemain.setTextColor(Color.argb(255,82,136,245));
        }else if (remain>3){
            holder.mTvRemain.setTextColor(Color.argb(255,117,117,117));
        }


        //#5288F5
        //????????????Item
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //??????
                Vibrator vibrator=(Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                //????????????
                Pair pairTvDate=new Pair<>(holder.mTvDate,"tvTime");
                Pair pairTvObject=new Pair<>(holder.mTvObject,"editObject");
                Pair pairTvEvent=new Pair<>(holder.mTvEvent,"editEvent");
                Pair pairItem=new Pair<>(holder.mVgItem,"Item");
                Pair pairCard=new Pair<>(holder.mCardView,"Card");

                /*Intent intent=new Intent(mContext, EditActivity.class);
                intent.putExtra("note",note);
                ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,pairCard);
                mContext.startActivity(intent,activityOptions.toBundle());*/


                Intent intent=new Intent(mContext,EditActivity.class);
                intent.putExtra("note",note);
                mContext.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle());

            }
        });

        //????????????Item
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                // ????????????
                Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                View dialogView = mLayoutInflater.inflate(R.layout.list_item_dialog_layout, null);

                //dialog??????
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                WindowManager.LayoutParams layoutParams=dialog.getWindow().getAttributes();
                layoutParams.dimAmount=0.0f;
                dialog.getWindow().setAttributes(layoutParams);

                TextView tvDelete = dialogView.findViewById(R.id.tv_delete);
                TextView tvEdit = dialogView.findViewById(R.id.tv_edit);

                //????????????
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        //??????
                        Vibrator vibrator=(Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                        deletItem(note,position);
                        dialog.dismiss();
                        new SendBroadcast().SendBroaCcast(mContext);
                    }
                });

                //????????????
                tvEdit.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        //??????
                        Vibrator vibrator=(Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                        Intent intent = new Intent(mContext, EditActivity.class);//????????? notice
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

        //??????????????????
        holder.mIbRemove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //??????
                Vibrator vibrator=(Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                deletItem(note,position);
                new SendBroadcast().SendBroaCcast(mContext);
            }
        });

        //????????????????????????
        holder.mCardViewSlide.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //??????
                Vibrator vibrator=(Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));

                deletItem(note,position);
                new SendBroadcast().SendBroaCcast(mContext);
            }
        });


    }//bindMyViewHolder

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void refreshData(List<Note> notes){
        this.mBeanList=notes;
        notifyDataSetChanged();
    }

    //??????Item
    private void deletItem(Note note,int position){
        int row=mNoteDbOpenHelper.deleteFromDbById(note.getId()+"");
        if (row>0){
            removeData(position);
        }

        new SendBroadcast().SendBroaCcast(mContext);
    }
    private void removeData(int position) {
        mBeanList.remove(position);

        //??????remove??????
        notifyItemRemoved(position);
        //????????????????????????Item?????????Item????????????notifyItemRangeRemoved??????????????????Item????????????
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
        CardView mCardViewSlide;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvObject=itemView.findViewById(R.id.tv_item_object);
            this.mTvEvent=itemView.findViewById(R.id.tv_item_event);
            this.mTvRemain=itemView.findViewById(R.id.tv_item_remain);
            this.mTvDate=itemView.findViewById(R.id.tv_item_date);
            this.mIbRemove=itemView.findViewById(R.id.remove_item);
            this.mVgItem=itemView.findViewById(R.id.LinearLayout_item);
            this.mCardView=itemView.findViewById(R.id.card_item_main);
            this.mCardViewSlide=itemView.findViewById(R.id.card_item_main_slide);
        }
    }

    public int day_minus(int year_start, int month_start, int day_start, int year_end, int month_end, int day_end) {
        int y2, m2, d2;
        int y1, m1, d1;
        /*??????????????????????????????3??????2???????????????????????????????????????????????????3??????????????????*/
        m1 = (month_start + 9) % 12;
        /*?????????1??????2????????????????????????????????????????????????0???3???1???????????????*/
        y1 = year_start - m1 / 10;
    /*365*y1 ?????????????????????????????????????????? y1/4 - y1/100 + y1/400  ????????????????????????????????????
    (day_start - 1) ????????????????????????1?????????????????????*/
        d1 = 365 * y1 + y1 / 4 - y1 / 100 + y1 / 400 + (m1 * 306 + 5) / 10 + (day_start - 1);
        /*(m2*306 + 5)/10 ???????????????????????????3???1??????????????? 306=365-31-28???1??????2?????????5??????????????????31??????????????????*/
        m2 = (month_end + 9) % 12;
        y2 = year_end - m2 / 10;
        d2 = 365 * y2 + y2 / 4 - y2 / 100 + y2 / 400 + (m2 * 306 + 5) / 10 + (day_end - 1);
        return (d2 - d1);
    }

    /*public void sendBroadcast(){
        // ????????????????????????????????????????????????????????????????????????
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
//                            e.printStackTrace();
                } finally {
                    // ??????????????????
                    Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE", null, mContext, WidgetSingleObject22.class);
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{0});
                    mContext.sendBroadcast(intent);
                }
            }
        }.start();
    }*/

}
