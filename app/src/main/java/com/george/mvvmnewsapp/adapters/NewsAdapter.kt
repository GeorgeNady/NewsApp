package com.george.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.george.mvvmnewsapp.R
import com.george.mvvmnewsapp.models.Article
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticlesViewHolder>() {

    // ---------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------- ViewHolder
    inner class ArticlesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // -----------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------- Differ

    /*
        what you should always do is to implement your #RecyclerViewArbter with #Diff_Utile
        normally if you had a recycler view adapter and you pass like list of articles
        // class NewsAdapter (
        // list: List<Article>
        // ): RecyclerView.Adapter<NewsAdapter.ArticlesViewHolder> () {
        every time you want to add an article
            * then you add it yo th list
            * and you will call Arapter.Notify data set changed
            but that is very inefficient because by using this #Adabter.notifyDataSetChanged
            - ( the recycler view adapter will always update its whole items even the items that did not changed )
            and we want to do that in this bart
     */
    /*
    * to solve this problem we can use what is call #Diff_Utile
    * #Diff_Utile calculate the differences between two lists and also enable us to update those items that will different
    * and another advantage of that
    *** it will happen in the background ==> so we don't block our main thread
    */

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // second step to create #async_list_differ
    // this is a tool it take our #two #lists and #compares them and calculate the #differentces
    // its #async so #asynchronous so it will run in the back ground
    val differ = AsyncListDiffer(this, differCallBack)

    // ---------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------- recycler view function
    // we can now implement our recycler view function
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        return ArticlesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        // we need to return the #amount of items that it all items in our itemView
        // and because we don't have that list that we passed in our constractor instead we use our #list_Differ
        // so we need to get our item count from our #list_differ
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }

    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
