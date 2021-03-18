package com.example.mvvmarchitectureexample.ui.common;

import android.os.AsyncTask;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//T--> tipo de item en la lista
//V--> tipo de view data binding
public abstract class DataBoundListAdapter<T, V extends ViewDataBinding>
        extends RecyclerView.Adapter<DataBoundViewHolder<V>> {
    private List<T> listItems;
    private int dataVersion = 0;

    @Override
    public final DataBoundViewHolder<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        V binding = createBinding(parent);
        return new DataBoundViewHolder<>(binding);
    }

    protected abstract V createBinding(ViewGroup parent);

    @Override
    public final void onBindViewHolder(DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, listItems.get(position));
        holder.binding.executePendingBindings();
    }

    protected abstract void bind(V binding, T item);

    @MainThread
    public void replace(List<T> updateList) {
        dataVersion++;

        if(listItems == null) {
            if(!(updateList == null)) {
                listItems = updateList;
                notifyDataSetChanged();
            }
        } else if(updateList == null) {
            int itemsOldSize = listItems.size();
            int startPositicion = 0;
            listItems = null;
            notifyItemRangeChanged(startPositicion, itemsOldSize);
        } else {
            final int startVersion = dataVersion;
            final List<T> oldItems = listItems;

            new AsyncTask<Void, Void, DiffUtil.DiffResult >() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return updateList.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = updateList.get(newItemPosition);

                            return DataBoundListAdapter.this.areItemsTheSame(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            T oldItem = oldItems.get(oldItemPosition);
                            T newItem = updateList.get(newItemPosition);

                            return DataBoundListAdapter.this.areContentsTheSame(oldItem, newItem);                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if(startVersion == dataVersion) {
                        listItems = updateList;
                    }
                }
            }.execute();
        }
    }

    protected abstract boolean areItemsTheSame(T oldItem, T newItem);

    protected abstract boolean areContentsTheSame(T oldItem, T newItem);

    @Override
    public int getItemCount(){
        int itemCount = listItems == null ? 0 : listItems.size();

        return itemCount;
    }
}
