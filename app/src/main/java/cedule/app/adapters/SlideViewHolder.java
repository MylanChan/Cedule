package cedule.app.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import cedule.app.R;

public class SlideViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private Context context;

    public void setData(int id) {
        Glide.with(context)
                .load(id)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into((ImageView) view.findViewById(R.id.iv_image));
    }

    public SlideViewHolder(Context context, @NonNull View itemView) {
        super(itemView);

        this.view = itemView;
        this.context = context;
    }
}
