package com.example.projektdyplomowyankiety;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    private LayoutInflater li;
    private List<String> days = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private List<String> notificationSurveysName = new ArrayList<>();


    public NotificationAdapter(Context context, List<String> s, List<String> days1, List<String> timeOfNotif) {

        days = days1;
        times = timeOfNotif;
        notificationSurveysName = s;
        mContext = context;
        li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_alarm_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.survName.setText(notificationSurveysName.get(position));
        holder.notifTime.setText(times.get(position));
        holder.repeatDays.setText(days.get(position));

        holder.deleteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                                db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Notif/"
                                        + notificationSurveysName.get(position) + "/days").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                            int requestCode = q.getLong("hashCode").intValue();

                                            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                            Intent myIntent = new Intent(mContext, ReminderBroadcast.class);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                                    mContext, requestCode, myIntent, 0);
                                            alarmManager.cancel(pendingIntent);

                                        }

                                        db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Notif/"
                                                + notificationSurveysName.get(position) + "/days").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot q : queryDocumentSnapshots) {
                                                    q.getReference().delete();
                                                }
                                            }
                                        });

                                        db.collection("Users/" + currentFirebaseUser.getUid() + "/Created_Notif/"
                                        ).document(notificationSurveysName.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                removeItemAndNotify(position);
                                            }
                                        });
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.strdeleteNotif)).setPositiveButton(mContext.getResources().getString(R.string.strYes), dialogClickListener)
                        .setNegativeButton(mContext.getResources().getString(R.string.strNo), dialogClickListener).show();
            }
        });

    }

    private void removeItemAndNotify(int position) {
        notificationSurveysName.remove(position);
        days.remove(position);
        times.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notificationSurveysName.size());
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return notificationSurveysName.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView survName;
        TextView notifTime;
        TextView repeatDays;
        ImageView deleteNotification;

        public ViewHolder(View itemView) {
            super(itemView);
            survName = itemView.findViewById(R.id.recycle_title);
            notifTime = itemView.findViewById(R.id.recycle_date_time);
            repeatDays = itemView.findViewById(R.id.recycle_repeat_info);
            deleteNotification = itemView.findViewById(R.id.deletNotific);
        }
    }


}
