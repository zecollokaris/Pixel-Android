package Animations;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleAnimation extends ItemTouchHelper.Callback {
    private final Movements movements;

    public SimpleAnimation(Movements movements) {
        this.movements = movements;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags= ItemTouchHelper.START| ItemTouchHelper.END;
        return makeMovementFlags(0,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        movements.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public  void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionChange){
        if(actionChange!= ItemTouchHelper.ACTION_STATE_IDLE){
            if (viewHolder instanceof ItemMovement){
                ItemMovement itemMovement=(ItemMovement) viewHolder;
                itemMovement.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder,actionChange);
    }


    @Override
    public  void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
        if(viewHolder instanceof ItemMovement){
            ItemMovement itemMovement=(ItemMovement) viewHolder;
            itemMovement.onItemClear();
        }
        super.clearView(recyclerView,viewHolder);
    }
}
