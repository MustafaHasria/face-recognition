package com.omar.facerecognitionapps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.helpers.OnClickItem;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewStudentsAdapter extends RecyclerView.Adapter<ViewStudentsAdapter.VH> {
    private ArrayList<Student> listStudents = new ArrayList<>();
    private final Context ctx;

    public ViewStudentsAdapter(Context ctx) {
        this.ctx = ctx;
    }
    private OnClickItem onClickItem;

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public void setListStudents(ArrayList<Student> listStudents) {
        this.listStudents = listStudents;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_student, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        Student student = listStudents.get(position);
        holder.bind(student, ctx,onClickItem);
    }

    @Override
    public int getItemCount() {
        return listStudents.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private final TextView full_name, spec;
        private final CircleImageView img;

        public VH(@NonNull @NotNull View itemView) {
            super(itemView);
            full_name = itemView.findViewById(R.id.full_name_student);
            spec = itemView.findViewById(R.id.spec_student);
            img = itemView.findViewById(R.id.img_student);
        }

        public void bind(Student student, Context ctx,OnClickItem onClickItem) {
            String first = Utils.upperFirst(student.getFirst());
            String last = Utils.upperFirst(student.getLast());
            String fullName = first + " " + last;
            full_name.setText(fullName);
            spec.setText(student.getSpec());
            Glide.with(ctx).load(student.getImage()).into(img);
            itemView.setOnClickListener(item->{
                onClickItem.onClick(getAdapterPosition());
            });
        }
    }
}
