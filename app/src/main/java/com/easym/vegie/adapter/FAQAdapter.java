package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.model.faq.GetFAQData;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyViewHolder> {
    private Context context;
    private List<GetFAQData> faqData;
    private static int currentPosition = 0;
    public FAQAdapter(Context context, List<GetFAQData> faqList) {
        this.context = context;
        this.faqData = faqList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_Question.setText(faqData.get(position).getQuestion());
        holder.tv_Answer.setText(faqData.get(position).getAnswer());
        holder.hide_layout.setVisibility(View.GONE);

        //if the position is equals to the item position which is to be expanded

            //creating an animation
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down_animation);

            //toggling visibility
            holder.hide_layout.setVisibility(View.VISIBLE);

            //adding sliding effect
            holder.hide_layout.startAnimation(slideDown);


    }

    @Override
    public int getItemCount() {
        return faqData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_expand,hide_layout;
        TextView tv_Question,tv_Answer;
        ImageView hide_image;
        View expand_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_expand = itemView.findViewById(R.id.ll_expand);
            hide_layout = itemView.findViewById(R.id.hide_layout);
            tv_Question = itemView.findViewById(R.id.tv_Question);
            tv_Answer = itemView.findViewById(R.id.tv_Answer);
            hide_image = itemView.findViewById(R.id.hide_image);
            expand_view = itemView.findViewById(R.id.expand_view);

        }
    }

}
