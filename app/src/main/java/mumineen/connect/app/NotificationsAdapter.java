package mumineen.connect.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.CustomViewHolder> {

    private Activity mActivity;
    private ArrayList<NotificationPOJO> mNotificationses;

    NotificationsAdapter(NotificationActivity notificationActivity) {
        mActivity = notificationActivity;
        mNotificationses = NotificationActivity.mNotificationses;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_view, null);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {


        if (!TextUtils.isEmpty(mNotificationses.get(position).mNotificationImageUrl)) {
            holder.mCardViewTexts.setVisibility(View.GONE);
            holder.mCardViewImage.setVisibility(View.VISIBLE);

            holder.mCardViewImage.setOnClickListener(onClickListener);
            holder.mCardViewImage.setTag(holder);

            holder.progress.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(mActivity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

            Picasso.get()
                    .load(mNotificationses.get(position).mNotificationImageUrl)
                    .error(R.drawable.ic_menu_alerts_push)
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

            holder.mTextViewDateImage.setText(mNotificationses.get(position).mNotificationDateTime);

        } else {

            holder.mCardViewTexts.setVisibility(View.VISIBLE);
            holder.mCardViewImage.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(mNotificationses.get(position).mNotificationTitle)) {
                holder.mTextViewNotificationName.setText(mNotificationses.get(position).mNotificationTitle);
                holder.mCardViewTexts.setOnClickListener(onClickListener);
                holder.mCardViewTexts.setTag(holder);
                holder.mTextViewDate.setText(mNotificationses.get(position).mNotificationDateTime);
            } else
                holder.mCardViewTexts.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mNotificationses == null ? 0 : mNotificationses.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNotificationName, mTextViewDate, mTextViewDateImage;
        CardView mCardViewTexts;
        CardView mCardViewImage;
        AppCompatImageView mImageView;
        ProgressBar progress;

        CustomViewHolder(View itemView) {
            super(itemView);
            mCardViewTexts = itemView.findViewById(R.id.card_view_texts);
            mCardViewImage = itemView.findViewById(R.id.card_view_image);
            mTextViewNotificationName = itemView.findViewById(R.id.text_notification_name);
            mTextViewDate = itemView.findViewById(R.id.text_notification_date);
            mImageView = itemView.findViewById(R.id.item_image);
            mTextViewDateImage = itemView.findViewById(R.id.text_notification_date_image);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CustomViewHolder customViewHolder = (CustomViewHolder) v.getTag();
            Intent intent = new Intent(mActivity, NotificationDetailsActivity.class);
            intent.putExtra("POSITION", customViewHolder.getAdapterPosition());
            mActivity.startActivity(intent);
        }
    };
}
