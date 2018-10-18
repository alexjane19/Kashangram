package io.github.froger.instamaterial.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.pojo.FeedItem;
import io.github.froger.instamaterial.ui.activity.MainActivity;
import io.github.froger.instamaterial.ui.utils.CircleTransformation;
import io.github.froger.instamaterial.ui.view.LoadingFeedItemView;

import static io.github.froger.instamaterial.ui.activity.BaseDrawerActivity.URL_UPLOADED_IMAGE;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    //private final List<FeedItem> feedItems = new ArrayList<>();
    List<io.github.froger.instamaterial.pojo.FeedItem> feedItems;
    public Context context;
    private OnFeedItemClickListener onFeedItemClickListener;

    private boolean showLoadingView = false;

    public FeedAdapter(Context context, List<io.github.froger.instamaterial.pojo.FeedItem> feedItems) {
        this.context = context;
        this.feedItems = feedItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }

        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition).addNlike();
                notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                feedItems.get(adapterPosition).addNlike();
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                if (context instanceof MainActivity) {
                    ((MainActivity) context).showLikedSnackbar();
                }
            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(view);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        CellFeedViewHolder cellFeedViewHolder = (CellFeedViewHolder) viewHolder;
        cellFeedViewHolder.bindView(feedItems.get(position));

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }else{
            Picasso.with(context)
                    .load(URL_UPLOADED_IMAGE + feedItems.get(position).getProfilePhoto() + ".jpg")
                    .placeholder(R.mipmap.profile/*R.drawable.img_circle_placeholder*/)
                    .resize(cellFeedViewHolder.avatarSize,cellFeedViewHolder.avatarSize)
                    .centerCrop()
                    .transform(new CircleTransformation())
                    .into(cellFeedViewHolder.ivUserProfile);


            Picasso.with(context)
                    .load(URL_UPLOADED_IMAGE + feedItems.get(position).getPhotoId() + ".jpg")
                    .fit()
                    .centerCrop()
                    .into(cellFeedViewHolder.ivFeedCenter);
            cellFeedViewHolder.tvFeedUserProfile.setText(feedItems.get(position).getUserId());
            cellFeedViewHolder.tvUserProfileCaption.setText(feedItems.get(position).getUserId());
            cellFeedViewHolder.tvFeedCaption.setText(feedItems.get(position).getCaption());
            ((CellFeedViewHolder) viewHolder).btnLike.setImageResource(feedItems.get(position).isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
            ((CellFeedViewHolder) viewHolder).tsLikesCounter.setCurrentText(((CellFeedViewHolder) viewHolder).vImageRoot.getResources().getQuantityString(
                    R.plurals.likes_count, feedItems.get(position).getNlike(), feedItems.get(position).getNlike()
            ));

        }
    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public void updateItems(boolean animated, List<io.github.froger.instamaterial.pojo.FeedItem> feedItems1) {
        //feedItems.clear();
        //feedItems.addAll(feedItems1);
//        feedItems.addAll(Arrays.asList(
//                new FeedItem(33, false),
//                new FeedItem(1, false),
//                new FeedItem(223, false),
//                new FeedItem(2, false),
//                new FeedItem(6, false),
//                new FeedItem(8, false),
//                new FeedItem(99, false)
//        ));
        if (animated) {
            notifyItemRangeInserted(0, feedItems.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
//        @BindView(R.id.ivFeedBottom)
//        ImageView ivFeedBottom;
        @BindView(R.id.btnComments)
        ImageButton btnComments;
        @BindView(R.id.btnLike)
        ImageButton btnLike;
        @BindView(R.id.btnMore)
        ImageButton btnMore;
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @BindView(R.id.tvFeedUserProfile)
        TextView tvFeedUserProfile;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @BindView(R.id.tvUserProfileCaption)
        TextView tvUserProfileCaption;
        @BindView(R.id.tvFeedCaption)
        TextView tvFeedCaption;

        @BindDimen(R.dimen.global_menu_avatar_size)
        int avatarSize;
        io.github.froger.instamaterial.pojo.FeedItem feedItem;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(io.github.froger.instamaterial.pojo.FeedItem feedItem) {
            this.feedItem = feedItem;

            //int adapterPosition = getAdapterPosition();

//            ivFeedCenter.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_center_1 : R.drawable.img_feed_center_2);
//            ivFeedBottom.setImageResource(adapterPosition % 2 == 0 ? R.drawable.img_feed_bottom_1 : R.drawable.img_feed_bottom_2);
//            btnLike.setImageResource(feedItem.isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
//            tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
//                    R.plurals.likes_count, feedItem.getNlike(), feedItem.getNlike()
//            ));
        }

        public io.github.froger.instamaterial.pojo.FeedItem getFeedItem() {
            return feedItem;
        }
    }

    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(io.github.froger.instamaterial.pojo.FeedItem feedItem) {
            super.bindView(feedItem);
        }
    }

    public static class FeedItem {
        public int likesCount;
        public boolean isLiked;

        public FeedItem(int likesCount, boolean isLiked) {
            this.likesCount = likesCount;
            this.isLiked = isLiked;
        }
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v);
    }
}
