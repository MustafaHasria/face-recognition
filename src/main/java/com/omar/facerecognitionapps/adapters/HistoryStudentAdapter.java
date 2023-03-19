package com.omar.facerecognitionapps.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.omar.facerecognitionapps.R;
import com.omar.facerecognitionapps.models.Student;
import com.omar.facerecognitionapps.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryStudentAdapter extends RecyclerView.Adapter<HistoryStudentAdapter.VH> {
    private final Context ctx;
    private final ArrayList<Student> listStudents = new ArrayList<>();
    private final ArrayList<String> listDate = new ArrayList<>();
    private HashMap<String, Student> studentHashMap = new HashMap<>();

    public HistoryStudentAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setHashMapStudents(HashMap<String, Student> listStudents) {
        this.studentHashMap = listStudents;
        convertHashToValues();
    }

    private void convertHashToValues() {
        listStudents.addAll(studentHashMap.values());
        listDate.addAll(studentHashMap.keySet());
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_history, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VH holder, int position) {
        Student student = listStudents.get(position);
        String date = listDate.get(position);
        holder.bind(student, ctx, date);
    }

    @Override
    public int getItemCount() {
        return listStudents.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView full_name, presence;
        private final CircleImageView img;

        public VH(@NonNull @NotNull View itemView) {
            super(itemView);
            full_name = itemView.findViewById(R.id.full_name_student);
            presence = itemView.findViewById(R.id.date_presence);
            img = itemView.findViewById(R.id.img_student);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Student student, Context ctx, String date) {
            String first = Utils.upperFirst(student.getFirst());
            String last = Utils.upperFirst(student.getLast());
            String fullName = first + " " + last;
            full_name.setText(fullName);
            presence.setText("Is present at: " + Utils.timeToLetter(date));
            Glide.with(ctx).load(student.getImage()).into(img);
        }
    }
}
