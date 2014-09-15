package com.github.amlcurran.showcaseview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

/**
 * Draws the text as required by the ShowcaseView
 */
class TextDrawer {

    private final TextPaint titlePaint;
    private final TextPaint textPaint;
    private final Context context;
    private final ShowcaseAreaCalculator calculator;
    private final float padding;
    private final float actionBarOffset;

    private CharSequence mTitle, mDetails;
    private float[] mBestTextPosition = new float[3];
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private TextAppearanceSpan mTitleSpan;
    private TextAppearanceSpan mDetailSpan;
    private boolean hasRecalculated;
    private Bitmap mBitmapImage;
    private Bitmap mBitmapIcon;

    public TextDrawer(Resources resources, ShowcaseAreaCalculator calculator, Context context) {
        padding = resources.getDimension(R.dimen.text_padding);
        actionBarOffset = resources.getDimension(R.dimen.action_bar_offset);

        this.calculator = calculator;
        this.context = context;

        titlePaint = new TextPaint();
        titlePaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
    }

    public void draw(Canvas canvas) {
        if (shouldDrawText()) {
        	float offsetForIconWidth = 0;
        	float offsetForTitleIConHeight = 0;
        	float offsetForDescription = 0;
            float[] textPosition = getBestTextPosition();

            if ( mBitmapIcon != null ) {
            	canvas.save();
    	        canvas.drawColor(Color.TRANSPARENT);
    	        canvas.drawBitmap(mBitmapIcon, textPosition[0],  textPosition[1], null);
    	        canvas.restore();
            }
            
            if (!TextUtils.isEmpty(mTitle)) {
                canvas.save();
                if (hasRecalculated) {
                    mDynamicTitleLayout = new DynamicLayout(mTitle, titlePaint,
                            (int) textPosition[2], Layout.Alignment.ALIGN_NORMAL,
                            1.0f, 1.0f, true);
                }
                
                //Multiply *2 for more space.
                offsetForIconWidth = mBitmapIcon != null ? mBitmapIcon.getWidth()+ mBitmapIcon.getWidth()/3  : 0;
                if (mDynamicTitleLayout != null) {
                    canvas.translate(textPosition[0] + offsetForIconWidth, textPosition[1]);
                    mDynamicTitleLayout.draw(canvas);
                    canvas.restore();
                }
            }

            if (!TextUtils.isEmpty(mDetails)) {
                canvas.save();
                if (hasRecalculated) {
                    mDynamicDetailLayout = new DynamicLayout(mDetails, textPaint,
                            (int) textPosition[2],
                            Layout.Alignment.ALIGN_NORMAL,
                            1.2f, 1.0f, true);
                }
                offsetForTitleIConHeight = getoffsetForTitleIcon();
                if (mDynamicDetailLayout != null) {
                    canvas.translate(textPosition[0], textPosition[1] + offsetForTitleIConHeight);
                    mDynamicDetailLayout.draw(canvas);
                    canvas.restore();
                }
            }
            if (mBitmapImage != null) {
            	offsetForDescription = mDynamicDetailLayout != null ? mDynamicDetailLayout.getHeight() : 0;
    	        canvas.save();
    	        canvas.drawColor(Color.TRANSPARENT);
    	        canvas.drawBitmap(mBitmapImage, textPosition[0],  textPosition[1] + offsetForTitleIConHeight + offsetForDescription , null);
    	        canvas.restore();
            }
        }
        hasRecalculated = false;
    }

    public void setContentText(CharSequence details) {
        if (details != null) {
            SpannableString ssbDetail = new SpannableString(details);
            ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
            mDetails = ssbDetail;
        }
    }

    public void setContentTitle(CharSequence title) {
        if (title != null) {
            SpannableString ssbTitle = new SpannableString(title);
            ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
            mTitle = ssbTitle;
        }
    }

    /**
     * Calculates the best place to position text
     *  @param canvasW width of the screen
     * @param canvasH height of the screen
     * @param shouldCentreText
     */
    public void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView, boolean shouldCentreText) {

    	Rect showcase = showcaseView.hasShowcaseView() ?
    			calculator.getShowcaseRect() :
    			new Rect();
    	
    	int[] areas = new int[4]; //left, top, right, bottom
    	areas[0] = showcase.left * canvasH;
    	areas[1] = showcase.top * canvasW;
    	areas[2] = (canvasW - showcase.right) * canvasH;
    	areas[3] = (canvasH - showcase.bottom) * canvasW;
    	
    	int largest = 0;
    	for(int i = 1; i < areas.length; i++) {
    		if(areas[i] > areas[largest])
    			largest = i;
    	}
    	
    	// Position text in largest area
    	switch(largest) {
    	case 0:
    		mBestTextPosition[0] = padding;
    		mBestTextPosition[1] = padding;
    		mBestTextPosition[2] = showcase.left - 2 * padding;
    		break;
    	case 1:
    		mBestTextPosition[0] = padding;
    		mBestTextPosition[1] = padding + actionBarOffset;
    		mBestTextPosition[2] = canvasW - 2 * padding;
    		break;
    	case 2:
    		mBestTextPosition[0] = showcase.right + padding;
    		mBestTextPosition[1] = padding;
    		mBestTextPosition[2] = (canvasW - showcase.right) - 2 * padding;
    		break;
    	case 3:
    		mBestTextPosition[0] = padding;
    		mBestTextPosition[1] = showcase.bottom + padding;
    		mBestTextPosition[2] = canvasW - 2 * padding;
    		break;
    	}
    	if(shouldCentreText) {
	    	// Center text vertically or horizontally
	    	switch(largest) {
	    	case 0:
	    	case 2:
	    		mBestTextPosition[1] += canvasH / 4;
	    		break;
	    	case 1:
	    	case 3:
	    		mBestTextPosition[2] /= 2;
	    		mBestTextPosition[0] += canvasW / 4;
	    		break;
	    	} 
    	} else {
    		// As text is not centered add actionbar padding if the text is left or right
	    	switch(largest) {
	    		case 0:
	    		case 2:
	    			mBestTextPosition[1] += actionBarOffset;
	    			break;
	    	}
    	}

        hasRecalculated = true;
    }

    public void setTitleStyling(int styleId) {
        mTitleSpan = new TextAppearanceSpan(this.context, styleId);
        setContentTitle(mTitle);
    }

    public void setDetailStyling(int styleId) {
        mDetailSpan = new TextAppearanceSpan(this.context, styleId);
        setContentText(mDetails);
    }

    public CharSequence getContentTitle() {
        return mTitle;
    }

    public CharSequence getContentText() {
        return mDetails;
    }
    
    public void setImage(int res) {
		mBitmapImage = BitmapFactory.decodeResource(this.context.getResources(), res);
	}
    
    public void setIcon(int res) {
    	mBitmapIcon = BitmapFactory.decodeResource(this.context.getResources(), res);
    }

    public float[] getBestTextPosition() {
    	// Prevent negative values
    	for ( int i=0;i<mBestTextPosition.length;i++ ) {
    		mBestTextPosition[i] = mBestTextPosition[i] < 0 ? 0 : mBestTextPosition[i];
    	}
        return mBestTextPosition;
    }

    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mTitle) || !TextUtils.isEmpty(mDetails);
    }
    
    /**
     * Get bigger offset between the icon and title. Both are in the same line. In case of not title and icon, will be 0.
     * @return
     */
    private int getoffsetForTitleIcon() {
    	if ( mBitmapIcon != null && mDynamicTitleLayout == null ) 
    		return mBitmapIcon.getHeight() + mBitmapIcon.getHeight()/3;
    	if ( mBitmapIcon == null && mDynamicTitleLayout != null ) 
    		return mDynamicTitleLayout.getHeight() + mDynamicTitleLayout.getHeight()/3;
    	if ( mBitmapIcon != null && mDynamicTitleLayout != null ) {
    		if ( mBitmapIcon.getHeight() > mDynamicTitleLayout.getHeight() )
    			return mBitmapIcon.getHeight() + mBitmapIcon.getHeight()/3;
    		else 
    			return mDynamicTitleLayout.getHeight() + mDynamicTitleLayout.getHeight()/3;
    	}
    	return 0;
    }
}
