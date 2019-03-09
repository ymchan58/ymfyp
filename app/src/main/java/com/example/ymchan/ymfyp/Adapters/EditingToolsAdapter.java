package com.example.ymchan.ymfyp.Adapters;

/**
 * Created by yan min on 4/7/2018
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ymchan.ymfyp.R;
import com.example.ymchan.ymfyp.Util.ToolType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        //added by ym 31/1/2019 to implement ucrop in:
//        mToolList.add(new ToolModel("Crop", R.drawable.ic_iconfinder_edit_crop_3671687, ToolType.CROP));
        mToolList.add(new ToolModel("Rotate", R.drawable.ic_iconfinder_rotate_left_1608422, ToolType.ROTATE));
        mToolList.add(new ToolModel("Adjust", R.drawable.ic_iconfinder_sun_symbol_110801, ToolType.ADJUST));

        //default tools:
        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));

        //added by ym 31/1/2019 to implement location sticker in:
        mToolList.add(new ToolModel("Location", R.drawable.ic_iconfinder_icon_location_2867917, ToolType.LOCATION));
    }

//    public EditingToolsAdapter() {
//        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush, ToolType.BRUSH));
//        mToolList.add(new ToolModel("Text", R.drawable.ic_text, ToolType.TEXT));
//        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER));
//        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER));
//        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI));
//        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER));
//    }


    public interface OnItemSelected {
        void onToolSelected(ToolType toolType);
    }

    class ToolModel {
        private String mToolName;
        private int mToolIcon;
        private ToolType mToolType;

        ToolModel(String toolName, int toolIcon, ToolType toolType) {
            mToolName = toolName;
            mToolIcon = toolIcon;
            mToolType = toolType;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemSelected.onToolSelected(mToolList.get(getLayoutPosition()).mToolType);
                }
            });
        }
    }
}

