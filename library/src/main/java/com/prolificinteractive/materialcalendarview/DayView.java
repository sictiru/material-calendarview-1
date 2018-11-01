package com.prolificinteractive.materialcalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView.ShowOtherDates;
import com.prolificinteractive.materialcalendarview.format.DayFormatter;

import java.util.List;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showDecoratedDisabled;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOtherMonths;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.showOutOfRange;

/**
 * Display one day of a {@linkplain MaterialCalendarView}
 */
@SuppressLint("ViewConstructor")
class DayView extends FrameLayout {

    private final Rect tempRect = new Rect();
    private final Rect circleDrawableRect = new Rect();
    private CalendarDay date;
    private int selectionColor = Color.GRAY;
    private int fadeTime;
    private Drawable customBackground = null;
    private Drawable selectionDrawable;
    private Drawable mCircleDrawable;
    private DayFormatter formatter = DayFormatter.DEFAULT;
    private DayFormatter contentDescriptionFormatter = formatter;
    private boolean isInRange = true;
    private boolean isInMonth = true;
    private boolean isDecoratedDisabled = false;
    private CheckedTextView textDay;
    private TextView textDayExtra;
    private boolean checked;
    private boolean disabled;
    private boolean disablePastDates;
    @ShowOtherDates
    private int showOtherDates = MaterialCalendarView.SHOW_DEFAULTS;

    public DayView(@NonNull Context context) {
        super(context);
        init(context);
    }


    public DayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DayView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public DayView(Context context, CalendarDay day, boolean disablePastDates) {
        super(context);
        init(context);

        setDay(day, disablePastDates);
    }

    private static Drawable generateBackground(int color, int fadeTime, Rect bounds) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.setExitFadeDuration(fadeTime);
        drawable.addState(new int[]{android.R.attr.state_checked}, generateCircleDrawable(color));

        return drawable;
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_day, this);
        textDay = findViewById(R.id.text_day);
        textDayExtra = findViewById(R.id.text_info);

        setSelectionColor(this.selectionColor);
    }

    public void setDay(CalendarDay date, boolean disablePastDates) {
        this.date = date;
        this.disablePastDates = disablePastDates;
        textDay.setText(getDayText());
        if (getTextDay().length() == 1) {
            textDay.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10));
        }
    }

    /**
     * Set the new label formatter and reformat the current label. This preserves current spans.
     *
     * @param formatter new label formatter
     */
    public void setDayFormatter(DayFormatter formatter) {
        this.contentDescriptionFormatter = contentDescriptionFormatter == this.formatter ? formatter : contentDescriptionFormatter;
        this.formatter = formatter == null ? DayFormatter.DEFAULT : formatter;
        CharSequence currentLabel = textDay.getText();

        Object[] spans = null;
        if (currentLabel instanceof Spanned) {
            spans = ((Spanned) currentLabel).getSpans(0, currentLabel.length(), Object.class);
        }
        SpannableString newLabel = new SpannableString(getDayText());
        if (spans != null) {
            for (Object span : spans) {
                newLabel.setSpan(span, 0, newLabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textDay.setText(newLabel);
    }

    @NonNull
    public String getDayText() {
        return formatter.format(date);
    }

    public void setSelectionColor(int color) {
        this.selectionColor = color;
        regenerateBackground();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        textDay.setChecked(checked);
        this.checked = checked;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public TextView getTextDay() {
        return textDay;
    }

    public TextView getTextDayExtra() {
        return textDayExtra;
    }

    /**
     * @param drawable custom selection drawable
     */
    public void setSelectionDrawable(Drawable drawable) {
        if (drawable == null) {
            this.selectionDrawable = null;
        } else {
            this.selectionDrawable = drawable.getConstantState().newDrawable(getResources());
        }
        regenerateBackground();
    }

    /**
     * @param drawable background to draw behind everything else
     */
    public void setCustomBackground(Drawable drawable) {
        if (drawable == null) {
            this.customBackground = null;
        } else {
            this.customBackground = drawable.getConstantState().newDrawable(getResources());
        }
        invalidate();
    }

    public CalendarDay getDate() {
        return date;
    }

    private void setEnabled() {
        boolean enabled = isInMonth && isInRange && !isDecoratedDisabled;
        super.setEnabled(isInRange && !isDecoratedDisabled);

        boolean showOtherMonths = showOtherMonths(showOtherDates);
        boolean showOutOfRange = showOutOfRange(showOtherDates) || showOtherMonths;
        boolean showDecoratedDisabled = showDecoratedDisabled(showOtherDates);

        boolean shouldBeVisible = enabled;

        if (!isInMonth && showOtherMonths) {
            shouldBeVisible = true;
        }

        if (!isInRange && showOutOfRange) {
            shouldBeVisible |= isInMonth;
        }

        if (isDecoratedDisabled && showDecoratedDisabled) {
            shouldBeVisible |= isInMonth && isInRange;
        }

        if (disablePastDates && date.isBefore(CalendarDay.today())) {
            textDay.setTextColor(Color.parseColor("#a7a6a6"));
        } else if (!isInMonth && shouldBeVisible) {
            textDay.setTextColor(textDay.getTextColors().getColorForState(
                    new int[]{-android.R.attr.state_enabled}, Color.GRAY));
        }
        setVisibility(shouldBeVisible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setupSelection(@ShowOtherDates int showOtherDates, boolean inRange, boolean inMonth) {
        this.showOtherDates = showOtherDates;
        this.isInMonth = inMonth;
        this.isInRange = inRange;
        setEnabled();
    }

    public void setDayFormatterContentDescription(DayFormatter formatter) {
        this.contentDescriptionFormatter = formatter == null ? this.formatter : formatter;
        setContentDescription(getContentDescriptionLabel());
    }

    public String getContentDescriptionLabel() {
        return contentDescriptionFormatter == null ? formatter.format(date) : contentDescriptionFormatter.format(date);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (customBackground != null) {
            customBackground.setBounds(tempRect);
            customBackground.setState(getDrawableState());
            customBackground.draw(canvas);
        }

        mCircleDrawable.setBounds(circleDrawableRect);

        super.onDraw(canvas);
    }

    private void regenerateBackground() {
        if (selectionDrawable != null) {
            textDay.setBackground(selectionDrawable);
        } else {
            mCircleDrawable = generateBackground(selectionColor, fadeTime, circleDrawableRect);
            textDay.setBackground(mCircleDrawable);
        }
    }

    /**
     * @param facade apply the facade to us
     */
    void applyFacade(DayViewFacade facade) {
        this.isDecoratedDisabled = facade.areDaysDisabled();
        setEnabled();

        setCustomBackground(facade.getBackgroundDrawable());
        setSelectionDrawable(facade.getSelectionDrawable());

        // Facade has spans
        List<DayViewFacade.Span> spans = facade.getSpans();
        if (!spans.isEmpty()) {
            String label = getDayText();
            SpannableString formattedLabel = new SpannableString(getDayText());
            for (DayViewFacade.Span span : spans) {
                formattedLabel.setSpan(span.span, 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textDay.setText(formattedLabel);
        }
        // Reset in case it was customized previously
        else {
            textDay.setText(getDayText());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calculateBounds(right - left, bottom - top);
        regenerateBackground();
    }

    private void calculateBounds(int width, int height) {
        final int radius = Math.min(height, width);
        final int offset = Math.abs(height - width) / 2;

        // Lollipop platform bug. Circle drawable offset needs to be half of normal offset
        final int circleOffset = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ? offset / 2 : offset;

        if (width >= height) {
            tempRect.set(offset, 0, radius + offset, height);
            circleDrawableRect.set(circleOffset, 0, radius + circleOffset, height);
        } else {
            tempRect.set(0, offset, width, radius + offset);
            circleDrawableRect.set(0, circleOffset, width, radius + circleOffset);
        }
    }
}
