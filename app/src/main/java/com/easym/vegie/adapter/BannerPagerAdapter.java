package com.easym.vegie.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.easym.vegie.R;
import com.easym.vegie.activities.ShopByCategoryActivity;
import com.easym.vegie.model.home.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerPagerAdapter extends PagerAdapter {

    private List<Banner> bannerList;
    private LayoutInflater inflater;
    private Context context;


    public BannerPagerAdapter(Context context, List<Banner> list) {
        this.context = context;
        this.bannerList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        View imageLayout = inflater.inflate(R.layout.banner_view_pager_item, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.iv_BannerImage);


        Picasso.get()
                .load(bannerList.get(position).getMobileBannerImage())
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_loading)
                .into(imageView);

        view.addView(imageLayout, 0);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ShopByCategoryActivity.class));

            }
        });

        return imageLayout;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
