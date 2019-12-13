package ru.unfortunately.school.tetris;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.unfortunately.school.tetris.RecordListAdapter.Holder;
import ru.unfortunately.school.tetris.room.Record;

public class RecordListAdapter extends RecyclerView.Adapter<Holder> {

    private List<Record> mRecords;

    public RecordListAdapter(@NonNull List<Record> records) {
        mRecords = new ArrayList<>(records);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_records, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Record record = mRecords.get(position);
        holder.bindView(
                position + 1,
                record.getNickname(),
                record.getScore(),
                record.getDate());
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public void setData(@NonNull List<Record> records){
        mRecords = new ArrayList<>(records);
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView mNumber;
        private TextView mNickname;
        private TextView mScore;
        private TextView mDate;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mNumber = itemView.findViewById(R.id.text_view_place_number);
            mNickname = itemView.findViewById(R.id.text_view_record_nickname);
            mScore = itemView.findViewById(R.id.text_view_record_score);
            mDate = itemView.findViewById(R.id.text_view_record_date);
        }

        private void bindView(int number, String nickname, int score, Date date){
            mNumber.setText(String.valueOf(number));
            mNickname.setText(nickname);
            mScore.setText(String.valueOf(score));
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            mDate.setText(format.format(date));
        }
    }
}
