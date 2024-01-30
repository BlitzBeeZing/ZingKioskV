package app.zingnow.zingkiosk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.zingnow.zingkiosk.R;
import app.zingnow.zingkiosk.model.Menu;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.OuterViewHolder> {
    ArrayList<Menu> order_items;
    private CartAdapterListener listener;


    public CartAdapter(ArrayList<Menu> order_items)
    {
        this.order_items = order_items;

    }

    public void setListener(CartAdapterListener listener) {
        this.listener = listener;
    }

    public interface CartAdapterListener {
        void onItemAddedOrRemoved(int position,Boolean is_added);

    }

    @NonNull
    @Override
    public OuterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_list, parent, false);
        return new OuterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OuterViewHolder holder, int position) {
        if (position >= 0 && position < order_items.size()) {

            Menu menu = order_items.get(position);

            holder.item_name.setText(menu.getNameOfItem());
            int price = (int)menu.getSellingPrice();
            holder.item_price.setText("₹ " + price);
            holder.item_quantity.setText(menu.getQuantity() + "");
            holder.item_description.setText(menu.getItemDescription());

            holder.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animateItem(view);
                    int quantity = menu.getQuantity();
                    menu.setQuantity(quantity + 1);
                    int adapter_position = holder.getAdapterPosition();
                    notifyItemChanged(adapter_position);
                    if (listener != null) {
                        listener.onItemAddedOrRemoved(adapter_position, true);
                    }
                }
            });
            holder.remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animateItem(view);
                    int quantity = menu.getQuantity();
                    int adapter_position = holder.getAdapterPosition();
                    if (adapter_position >= 0 && adapter_position < order_items.size()) {

                        if (quantity > 1) {
                            menu.setQuantity(quantity - 1);
                            notifyItemChanged(adapter_position);
                            if (listener != null) {
                                listener.onItemAddedOrRemoved(adapter_position, false);
                            }
                        } else {
                            // Remove the item from the list
                            notifyItemRemoved(adapter_position); // Notify adapter that item at this position is removed
                            notifyItemRangeChanged(adapter_position, order_items.size()); // Notify adapter about range change
                            order_items.remove(adapter_position);

                            if (listener != null) {
                                listener.onItemAddedOrRemoved(adapter_position, false);
                            }

                        }

                    }
                }
            });


        }
    }


    @Override
    public int getItemCount() {
        if(order_items.isEmpty())
            return 0;
        return order_items.size();
    }



    public class OuterViewHolder extends RecyclerView.ViewHolder {
        TextView item_name,item_price,item_quantity,item_description;
        ImageView add_btn,remove_btn;

        public OuterViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_quantity = itemView.findViewById(R.id.item_quantity);
            add_btn = itemView.findViewById(R.id.add_item);
            remove_btn = itemView.findViewById(R.id.delete_item);
            item_description = itemView.findViewById(R.id.item_description);
        }
    }


    private void animateItem(View view) {
        // Create a scale animation for zooming in
        Animation scaleInAnimation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleInAnimation.setDuration(150); // in milliseconds

        // Create an alpha animation for fading in
        Animation fadeInAnimation = new AlphaAnimation(1, 0.6f);
        fadeInAnimation.setDuration(120); // in milliseconds

        // Set up a listener for the animation set
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated, if set to repeat
            }
        };

        // Combine the scale and alpha animations into a set
        Animation animationSet = new AnimationSet(false);
        ((AnimationSet) animationSet).addAnimation(scaleInAnimation);
        ((AnimationSet) animationSet).addAnimation(fadeInAnimation);
        animationSet.setAnimationListener(animationListener);

        // Start the animation set on the clicked view
        view.startAnimation(animationSet);
    }

}
