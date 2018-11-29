package Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.valeen.finder.R;
import com.finder.valeen.finder.ReportActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import Animations.ItemMovement;
import Animations.Movements;
import Fragment.DeleteDialog;
import Models.Message;
import Models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<ViewHolder> implements  Movements{
    private ArrayList<Message> messages;
    private StorageReference storageReference;
    public Context context;
    FragmentManager fragmentManager;
    public MessagesAdapter(Context context, ArrayList<Message> messages, FragmentManager fragmentManager){
      this.context=context;
      this.messages=messages;
      this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.context=context;
        viewHolder.fullMessage=messages.get(i).getMessage();
        viewHolder.url=messages.get(i).getUrl();
        Glide.with(context)
                .asBitmap()
                .load(messages.get(i).getUrl())
                .into(viewHolder.circleImageView);
        String message;
        if(messages.get(i).getMessage().length()>30){
            message=messages.get(i).getMessage().substring(0,30).concat("...");
        }
        else{
            message=messages.get(i).getMessage();
        }
        viewHolder.txtMessage.setText(message);
        viewHolder.txtDate.setText(messages.get(i).getDate());
        viewHolder.data=Long.toString(messages.get(i).getTime());
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onItemDismiss(int position) {
        DeleteDialog deleteDialog=new DeleteDialog();
        deleteDialog.message="Are you sure you want to delete this?";
        deleteDialog.index=position;
        deleteDialog.messagesAdapter=this;
        deleteDialog.show(fragmentManager,"DELETE DIALOG");
    }
    public StorageReference deleteItem(long time) {
        storageReference = FirebaseStorage.getInstance().getReference().child(Long.toString(time));
        return storageReference;
    }

}


class  ViewHolder extends  RecyclerView.ViewHolder implements ItemMovement,View.OnClickListener {
    CircleImageView circleImageView;
    TextView txtMessage;
    TextView txtDate;
    String data;
    String url;
    View view;
    Context context;
    String fullMessage;
    ViewHolder(View view){
        super(view);
        this.view=view;
        this.view.setOnClickListener(this);
        circleImageView=(CircleImageView) view.findViewById(R.id.imgPic);
        txtMessage=(TextView) view.findViewById(R.id.txtMessage);
        txtDate=(TextView) view.findViewById(R.id.txtDate);
    }

    @Override
    public void onItemSelected() {
        view.animate().alpha(0.7f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(500);
    }

    @Override
    public void onItemClear() {
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f);
    }

    @Override
    public void onClick(View v) {
         if(v.equals(this.view)){
             Intent intent=new Intent(context,ReportActivity.class);
             intent.putExtra("url",url);
             intent.putExtra("description",fullMessage);
             intent.putExtra("time",data);
             context.startActivity(intent);
         }
    }
}