package app.zingnow.zingkiosk.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Delivery;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.OuterViewHolder>{

    ArrayList<Delivery> delivery_list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int selectedItem = -1; // Initially, no item is selected


    public interface OnItemClickListener {
        void onItemClick(Delivery delivery);
    }


    public DeliveryAdapter(ArrayList<Delivery> delivery_list, OnItemClickListener onItemClickListener)
    {
        this.delivery_list = delivery_list;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public DeliveryAdapter.OuterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_list, parent, false);
        return new OuterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAdapter.OuterViewHolder holder, int position) {
        Delivery delivery = delivery_list.get(position);



        if(delivery.getSelected())
        {
            holder.is_selected.setChecked(true);
        }
        else
        {
            holder.is_selected.setChecked(false);
        }
        holder.heading1.setText(delivery.getHeading1());
        holder.is_free.setText(delivery.getIsFree());
        holder.delivery_address.setText(delivery.getDeliveryAddress());
        holder.timeslot.setText(delivery.getTimeSlot());

        holder.list_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousSelectedItem = selectedItem;
                selectedItem = holder.getAdapterPosition();

                holder.is_selected.setChecked(true);
                delivery.setSelected(true);
                Log.e("CLICKED","HEH");

                notifyItemChanged(previousSelectedItem);
                notifyItemChanged(selectedItem);
            }
        });
        holder.is_selected.setChecked(position == selectedItem);


        holder.is_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked item to the onItemClick method
                int previousSelectedItem = selectedItem;
                selectedItem = holder.getAdapterPosition();

                holder.is_selected.setChecked(true);
                delivery.setSelected(true);
                Log.e("CLICKED","HEH");

                notifyItemChanged(previousSelectedItem);
                notifyItemChanged(selectedItem);

                onItemClickListener.onItemClick(delivery);
            }
        });
        holder.is_selected.setChecked(position == selectedItem);
    }

    @Override
    public int getItemCount() {
        return delivery_list.size();

    }

    public class OuterViewHolder extends RecyclerView.ViewHolder {
        RadioButton is_selected;
        TextView heading1,is_free,delivery_address,timeslot;
        RelativeLayout list_layout;
        public OuterViewHolder(@NonNull View itemView) {
            super(itemView);
            is_selected = itemView.findViewById(R.id.delivery_slot_selected);
            heading1 = itemView.findViewById(R.id.delivery_slot_text);
            is_free = itemView.findViewById(R.id.free_text);
            delivery_address = itemView.findViewById(R.id.delivery_address);
            timeslot = itemView.findViewById(R.id.timeslot);
            list_layout = itemView.findViewById(R.id.list_layout);




        }
    }

}
