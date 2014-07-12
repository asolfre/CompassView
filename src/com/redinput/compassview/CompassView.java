/*
 * Copyright 2014 RedInput
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redinput.compassview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

	private Paint mTextPaint, mMainLinePaint, mSecondaryLinePaint, mTerciaryLinePaint,
			mMarkerPaint;
	private Path pathMarker;

	private int mTextColor, mBackgroundColor, mLineColor, mMarkerColor;
	private float mDegrees, mTextSize, mRangeDegrees;
	private boolean mShowMarker;

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CompassView, 0,
				0);

		mBackgroundColor = a.getColor(R.styleable.CompassView_backgroundColor, Color.BLACK);
		mMarkerColor = a.getColor(R.styleable.CompassView_markerColor, Color.RED);
		mShowMarker = a.getBoolean(R.styleable.CompassView_showMarker, true);
		mLineColor = a.getColor(R.styleable.CompassView_lineColor, Color.WHITE);
		mTextColor = a.getColor(R.styleable.CompassView_textColor, Color.WHITE);
		mTextSize = a.getDimension(R.styleable.CompassView_textSize, 15 * getResources()
				.getDisplayMetrics().scaledDensity);
		mDegrees = a.getFloat(R.styleable.CompassView_degrees, 0);
		mRangeDegrees = a.getFloat(R.styleable.CompassView_rangeDegrees, 180f);

		a.recycle();

		checkValues();
		init();
	}

	private void checkValues() {
		if ((mDegrees < 0) || (mDegrees > 359))
			throw new IndexOutOfBoundsException(getResources()
					.getString(R.string.out_index_degrees));

		if ((mRangeDegrees < 90) || (mRangeDegrees > 360))
			throw new IndexOutOfBoundsException(getResources().getString(
					R.string.out_index_range_degrees));
	}

	private void init() {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.CENTER);

		mMainLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMainLinePaint.setStrokeWidth(8f);

		mSecondaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSecondaryLinePaint.setStrokeWidth(6f);

		mTerciaryLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTerciaryLinePaint.setStrokeWidth(3f);

		mMarkerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMarkerPaint.setStyle(Style.FILL);
		pathMarker = new Path();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle b = new Bundle();
		b.putParcelable("instanceState", super.onSaveInstanceState());
		b.putFloat("degrees", mDegrees);

		return b;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle b = (Bundle) state;
			mDegrees = b.getFloat("degrees", 0);

			state = b.getParcelable("instanceState");
		}

		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int minWidth = (int) Math.floor(50 * getResources().getDisplayMetrics().density);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;

		} else {
			result = minWidth + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int minHeight = (int) Math.floor(30 * getResources().getDisplayMetrics().density);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;

		} else {
			result = minHeight + getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);

		mMainLinePaint.setColor(mLineColor);
		mSecondaryLinePaint.setColor(mLineColor);
		mTerciaryLinePaint.setColor(mLineColor);

		mMarkerPaint.setColor(mMarkerColor);

		canvas.drawColor(mBackgroundColor);

		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();
		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();

		int unitHeight = (height - paddingTop - paddingBottom) / 12;

		float pixDeg = (width - paddingLeft - paddingRight) / mRangeDegrees;

		int minDegrees = Math.round(mDegrees - mRangeDegrees / 2), maxDegrees = Math.round(mDegrees
				+ mRangeDegrees / 2);

		for (int i = -180; i < 540; i += 15) {
			if ((i >= minDegrees) && (i <= maxDegrees)) {
				canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees), height - paddingBottom,
						paddingLeft + pixDeg * (i - minDegrees), 10 * unitHeight + paddingTop,
						mTerciaryLinePaint);

				if (i % 45 == 0) {
					canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
							height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
							8 * unitHeight + paddingTop, mSecondaryLinePaint);
				}

				if (i % 90 == 0) {
					canvas.drawLine(paddingLeft + pixDeg * (i - minDegrees),
							height - paddingBottom, paddingLeft + pixDeg * (i - minDegrees),
							6 * unitHeight + paddingTop, mMainLinePaint);

					String coord = "";
					switch (i) {
						case -90:
						case 270:
							coord = getResources().getString(R.string.compass_west);
							break;

						case 0:
						case 360:
							coord = getResources().getString(R.string.compass_north);
							break;

						case 90:
						case 450:
							coord = getResources().getString(R.string.compass_east);
							break;

						case -180:
						case 180:
							coord = getResources().getString(R.string.compass_south);
							break;
					}

					canvas.drawText(coord, paddingLeft + pixDeg * (i - minDegrees), 5 * unitHeight
							+ paddingTop, mTextPaint);
				}
			}
		}

		if (mShowMarker) {
			pathMarker.moveTo(width / 2, 3 * unitHeight + paddingTop);
			pathMarker.lineTo((width / 2) + 20, paddingTop);
			pathMarker.lineTo((width / 2) - 20, paddingTop);
			pathMarker.close();
			canvas.drawPath(pathMarker, mMarkerPaint);
		}
	}

	public void setDegrees(float degrees) {
		if ((mDegrees < 0) || (mDegrees > 359))
			throw new IndexOutOfBoundsException(getResources()
					.getString(R.string.out_index_degrees));

		mDegrees = degrees;
		invalidate();
		requestLayout();
	}

	@Override
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
		invalidate();
		requestLayout();
	}

	public void setLineColor(int color) {
		mLineColor = color;
		invalidate();
		requestLayout();
	}

	public void setMarkerColor(int color) {
		mMarkerColor = color;
		invalidate();
		requestLayout();
	}

	public void setTextColor(int color) {
		mTextColor = color;
		invalidate();
		requestLayout();
	}

	public void setShowMarker(boolean show) {
		mShowMarker = show;
		invalidate();
		requestLayout();
	}

	public void setTextSize(int size) {
		mTextSize = size;
		invalidate();
		requestLayout();
	}

	public void setRangeDegrees(float range) {
		if ((mRangeDegrees < 90) || (mRangeDegrees > 360))
			throw new IndexOutOfBoundsException(getResources().getString(
					R.string.out_index_range_degrees));

		mRangeDegrees = range;
		invalidate();
		requestLayout();
	}

}
