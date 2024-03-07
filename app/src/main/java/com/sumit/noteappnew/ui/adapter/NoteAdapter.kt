package com.sumit.noteappnew.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sumit.noteappnew.R
import com.sumit.noteappnew.data.local.models.LocalNote
import com.sumit.noteappnew.databinding.ItemNoteBinding

class NoteAdapter(val onItemClickListener: LocalNote.() -> Unit) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    val diffUtils = object : DiffUtil.ItemCallback<LocalNote>() {
        override fun areItemsTheSame(oldItem: LocalNote, newItem: LocalNote): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LocalNote, newItem: LocalNote): Boolean =
            oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffUtils)
    var notes: List<LocalNote>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener
        )

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.onBind(notes[position])
    }

    class NoteViewHolder(
        private val binding: ItemNoteBinding,
        val onItemClickListener: LocalNote.() -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(note: LocalNote) {
            with(binding) {
                noteText.isVisible = !note.noteTitle.isNullOrEmpty()
                noteDescription.isVisible = !note.description.isNullOrEmpty()

                note.noteTitle?.let {
                    noteText.text = it
                }

                note.description?.let {
                    noteDescription.text = it
                }

                noteSync.setBackgroundResource(
                    if (note.isConnected) R.drawable.synced
                    else R.drawable.not_sync
                )

                root.setOnClickListener { onItemClickListener(note) }
            }
        }
    }
}
