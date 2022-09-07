package org.techtown.face.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.databinding.ItemContainerRecentConversionBinding;
import org.techtown.face.listeners.ConversionListener;
import org.techtown.face.models.ChatMessage;
import org.techtown.face.models.User;

import java.util.List;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversionsAdapter(List<ChatMessage> chatMessages,ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
            holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentConversionBinding binding;

         ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }
        void setData(ChatMessage chatMessage){
             binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
             binding.textName.setText(chatMessage.conversionName);
             binding.textRecentMessage.setText(chatMessage.message);
             binding.getRoot().setOnClickListener(view -> {
                 User user = new User();
                 user.id = chatMessage.conversionId;
                 user.name = chatMessage.conversionName;
                 user.image = chatMessage.conversionImage;
                 conversionListener.onConversionClicked(user);
             });
        }
    }

    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
