package cedule.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cedule.app.R;

public class SlideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int[] imageUrlList = new int[] {R.drawable.drawable_task, R.drawable.drawable_settings};

    private Context context;

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holder_slide_view, parent, false);

        return new SlideViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SlideViewHolder) holder).setData(imageUrlList[position]);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public SlideAdapter(Context context) {
        this.context = context;
    }
}
