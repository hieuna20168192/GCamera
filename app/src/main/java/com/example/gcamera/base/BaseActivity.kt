package com.example.gcamera.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<V : ViewDataBinding> :
    AppCompatActivity() {
    protected lateinit var binding: V

    @get: LayoutRes
    protected abstract val layoutId: Int

    @get: StyleRes
    protected var themeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        themeId?.let {
            setTheme(it)
        }
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this@BaseActivity
        initComponents()
        initListeners()
    }

    protected abstract fun initComponents()
    protected abstract fun initListeners()
}
