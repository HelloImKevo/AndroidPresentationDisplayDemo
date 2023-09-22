package com.kevo.displaydemo.ui.carousel

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionHelper
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.R

/**
 * Carousel works within a MotionLayout to provide a simple recycler like pattern.
 * Based on a series of Transitions and callback to give you the ability to swap views.
 *
 * This is a copy+pasted version of the native [androidx.constraintlayout.helper.widget.Carousel]
 * that fixes a bug with the infinite loop mechanism in [transitionToIndex], where the last
 * image element in the carousel will not animate to transition back to the "first" element
 * in the carousel (this may have been fixed in newer versions of the constraint layout library).
 */
class Carousel : MotionHelper {

    private var adapter: Adapter? = null
    private val list = ArrayList<View>()
    private var previousIndex = 0

    /**
     * Returns the current index
     *
     * @return current index
     */
    var currentIndex = 0
        private set

    private var motionLayout: MotionLayout? = null
    private var firstViewReference = -1
    private var infiniteCarousel = false
    private var backwardTransition = -1
    private var forwardTransition = -1
    private var previousState = -1
    private var nextState = -1
    private var dampening = 0.9f
    private var startIndex = 0
    private var emptyViewBehavior = INVISIBLE
    private var touchUpMode = TOUCH_UP_IMMEDIATE_STOP
    private var velocityThreshold = 2f
    private var targetIndex = -1
    private var animateTargetDelay = 200

    /**
     * Adapter for a Carousel
     */
    interface Adapter {

        /**
         * Number of items you want to display in the Carousel
         *
         * @return number of items
         */
        fun count(): Int

        /**
         * Callback to populate the view for the given index
         */
        fun populate(view: View, index: Int)

        /**
         * Callback when we reach a new index
         */
        fun onNewItem(index: Int)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.Carousel)
            val n = a.indexCount
            for (i in 0 until n) {
                when (val attr = a.getIndex(i)) {
                    R.styleable.Carousel_carousel_firstView -> {
                        firstViewReference = a.getResourceId(attr, firstViewReference)
                    }

                    R.styleable.Carousel_carousel_backwardTransition -> {
                        backwardTransition = a.getResourceId(attr, backwardTransition)
                    }

                    R.styleable.Carousel_carousel_forwardTransition -> {
                        forwardTransition = a.getResourceId(attr, forwardTransition)
                    }

                    R.styleable.Carousel_carousel_emptyViewsBehavior -> {
                        emptyViewBehavior = a.getInt(attr, emptyViewBehavior)
                    }

                    R.styleable.Carousel_carousel_previousState -> {
                        previousState = a.getResourceId(attr, previousState)
                    }

                    R.styleable.Carousel_carousel_nextState -> {
                        nextState = a.getResourceId(attr, nextState)
                    }

                    R.styleable.Carousel_carousel_touchUp_dampeningFactor -> {
                        dampening = a.getFloat(attr, dampening)
                    }

                    R.styleable.Carousel_carousel_touchUpMode -> {
                        touchUpMode = a.getInt(attr, touchUpMode)
                    }

                    R.styleable.Carousel_carousel_touchUp_velocityThreshold -> {
                        velocityThreshold = a.getFloat(attr, velocityThreshold)
                    }

                    R.styleable.Carousel_carousel_infinite -> {
                        infiniteCarousel = a.getBoolean(attr, infiniteCarousel)
                    }
                }
            }
            a.recycle()
        }
    }

    fun setAdapter(adapter: Adapter?) {
        this.adapter = adapter
    }

    val count: Int
        /**
         * Returns the number of elements in the Carousel
         *
         * @return number of elements
         */
        get() = if (adapter != null) {
            adapter!!.count()
        } else 0

    /**
     * Transition the carousel to the given index, animating until we reach it.
     *
     * @param index index of the element we want to reach
     * @param delay animation duration for each individual transition to the next item, in ms
     */
    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
    fun transitionToIndex(index: Int, delay: Int) {
        targetIndex = Math.max(0, Math.min(count - 1, index))
        animateTargetDelay = Math.max(0, delay)
        motionLayout!!.setTransitionDuration(animateTargetDelay)
        // NOTE: This is the fix for the infinite image looping animation:
        // index != 0 && ...
        if (index != 0 && index < currentIndex) {
            motionLayout!!.transitionToState(previousState, animateTargetDelay)
        } else {
            motionLayout!!.transitionToState(nextState, animateTargetDelay)
        }
    }

    /**
     * Jump to the given index without any animation
     *
     * @param index index of the element we want to reach
     */
    @Suppress("unused", "ReplaceJavaStaticMethodWithKotlinAnalog")
    fun jumpToIndex(index: Int) {
        currentIndex = Math.max(0, Math.min(count - 1, index))
        refresh()
    }

    /**
     * Rebuilds the scene
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun refresh() {
        val count = list.size
        for (i in 0 until count) {
            val view = list[i]
            if (adapter!!.count() == 0) {
                updateViewVisibility(view, emptyViewBehavior)
            } else {
                updateViewVisibility(view, VISIBLE)
            }
        }
        motionLayout!!.rebuildScene()
        updateItems()
    }

    override fun onTransitionChange(
        motionLayout: MotionLayout,
        startId: Int,
        endId: Int,
        progress: Float
    ) {
        if (DEBUG) {
            println("onTransitionChange from $startId to $endId progress $progress")
        }
        lastStartId = startId
    }

    private var lastStartId = -1

    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
        previousIndex = currentIndex
        if (currentId == nextState) {
            currentIndex++
        } else if (currentId == previousState) {
            currentIndex--
        }
        if (infiniteCarousel) {
            if (currentIndex >= adapter!!.count()) {
                currentIndex = 0
            }
            if (currentIndex < 0) {
                currentIndex = adapter!!.count() - 1
            }
        } else {
            if (currentIndex >= adapter!!.count()) {
                currentIndex = adapter!!.count() - 1
            }
            if (currentIndex < 0) {
                currentIndex = 0
            }
        }
        if (previousIndex != currentIndex) {
            this.motionLayout!!.post(updateRunnable)
        }
    }

    @Suppress("unused")
    private fun enableAllTransitions(enable: Boolean) {
        val transitions = motionLayout!!.definedTransitions
        for (transition in transitions) {
            transition.isEnabled = enable
        }
    }

    private fun enableTransition(transitionID: Int, enable: Boolean): Boolean {
        if (transitionID == -1) {
            return false
        }
        if (motionLayout == null) {
            return false
        }
        val transition = motionLayout!!.getTransition(transitionID) ?: return false
        if (enable == transition.isEnabled) {
            return false
        }
        transition.isEnabled = enable
        return true
    }

    private var updateRunnable: Runnable = object : Runnable {
        override fun run() {
            motionLayout!!.progress = 0f
            updateItems()
            adapter!!.onNewItem(currentIndex)
            val velocity = motionLayout!!.velocity
            if (touchUpMode == TOUCH_UP_CARRY_ON && velocity > velocityThreshold && currentIndex < adapter!!.count() - 1) {
                val v = velocity * dampening
                if (currentIndex == 0 && previousIndex > currentIndex) {
                    // don't touch animate when reaching the first item
                    return
                }
                if (currentIndex == adapter!!.count() - 1 && previousIndex < currentIndex) {
                    // don't touch animate when reaching the last item
                    return
                }
                motionLayout!!.post {
                    motionLayout!!.touchAnimateTo(
                        MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE,
                        1f, v)
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        list.clear()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val container: MotionLayout = if (parent is MotionLayout) {
            parent as MotionLayout
        } else {
            return
        }

        list.clear()
        for (i in 0 until mCount) {
            val id = mIds[i]
            val view = container.getViewById(id)
            if (firstViewReference == id) {
                startIndex = i
            }
            list.add(view)
        }
        motionLayout = container
        // set up transitions if needed
        if (touchUpMode == TOUCH_UP_CARRY_ON) {
            val forward = motionLayout!!.getTransition(forwardTransition)
            forward?.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE)
            val backward = motionLayout!!.getTransition(backwardTransition)
            backward?.setOnTouchUp(MotionLayout.TOUCH_UP_DECELERATE_AND_COMPLETE)
        }
        updateItems()
    }

    /**
     * Update the view visibility on the different ConstraintSets
     */
    private fun updateViewVisibility(view: View, visibility: Int): Boolean {
        if (motionLayout == null) {
            return false
        }
        var needsMotionSceneRebuild = false
        val constraintSets = motionLayout!!.constraintSetIds
        for (constraintSet in constraintSets) {
            needsMotionSceneRebuild =
                    needsMotionSceneRebuild or updateViewVisibility(constraintSet, view, visibility)
        }
        return needsMotionSceneRebuild
    }

    private fun updateViewVisibility(constraintSetId: Int, view: View, visibility: Int): Boolean {
        val constraintSet = motionLayout!!.getConstraintSet(constraintSetId) ?: return false
        val constraint = constraintSet.getConstraint(view.id) ?: return false
        constraint.propertySet.mVisibilityMode = ConstraintSet.VISIBILITY_MODE_IGNORE
        view.visibility = visibility
        return true
    }

    private fun updateItems() {
        if (adapter == null) {
            return
        }
        if (motionLayout == null) {
            return
        }
        if (adapter!!.count() == 0) {
            return
        }
        if (DEBUG) {
            println("Update items, index: $currentIndex")
        }
        val viewCount = list.size
        for (i in 0 until viewCount) {
            // mIndex should map to i == startIndex
            val view = list[i]
            var index = currentIndex + i - startIndex
            if (infiniteCarousel) {
                if (index < 0) {
                    if (emptyViewBehavior != INVISIBLE) {
                        updateViewVisibility(view, emptyViewBehavior)
                    } else {
                        updateViewVisibility(view, VISIBLE)
                    }
                    if (index % adapter!!.count() == 0) {
                        adapter!!.populate(view, 0)
                    } else {
                        adapter!!.populate(view, adapter!!.count() + index % adapter!!.count())
                    }
                } else if (index >= adapter!!.count()) {
                    if (index == adapter!!.count()) {
                        index = 0
                    } else if (index > adapter!!.count()) {
                        index = index % adapter!!.count()
                    }
                    if (emptyViewBehavior != INVISIBLE) {
                        updateViewVisibility(view, emptyViewBehavior)
                    } else {
                        updateViewVisibility(view, VISIBLE)
                    }
                    adapter!!.populate(view, index)
                } else {
                    updateViewVisibility(view, VISIBLE)
                    adapter!!.populate(view, index)
                }
            } else {
                if (index < 0) {
                    updateViewVisibility(view, emptyViewBehavior)
                } else if (index >= adapter!!.count()) {
                    updateViewVisibility(view, emptyViewBehavior)
                } else {
                    updateViewVisibility(view, VISIBLE)
                    adapter!!.populate(view, index)
                }
            }
        }
        if (targetIndex != -1 && targetIndex != currentIndex) {
            motionLayout!!.post {
                motionLayout!!.setTransitionDuration(animateTargetDelay)
                if (targetIndex < currentIndex) {
                    motionLayout!!.transitionToState(previousState, animateTargetDelay)
                } else {
                    motionLayout!!.transitionToState(nextState, animateTargetDelay)
                }
            }
        } else if (targetIndex == currentIndex) {
            targetIndex = -1
        }
        if (backwardTransition == -1 || forwardTransition == -1) {
            Log.w(TAG, "No backward or forward transitions defined for Carousel!")
            return
        }
        if (infiniteCarousel) {
            return
        }
        val count = adapter!!.count()
        if (currentIndex == 0) {
            enableTransition(backwardTransition, false)
        } else {
            enableTransition(backwardTransition, true)
            motionLayout!!.setTransition(backwardTransition)
        }
        if (currentIndex == count - 1) {
            enableTransition(forwardTransition, false)
        } else {
            enableTransition(forwardTransition, true)
            motionLayout!!.setTransition(forwardTransition)
        }
    }

    companion object {

        private const val DEBUG = false
        private const val TAG = "Carousel"
        const val TOUCH_UP_IMMEDIATE_STOP = 1
        const val TOUCH_UP_CARRY_ON = 2
    }
}
