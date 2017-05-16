package com.ramotion.cardslider.example.simple;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int[][] dotCoords = new int[5][2];
    private final int[] pics = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5};
    private final int[] maps = {R.drawable.map_1, R.drawable.map_2, R.drawable.map_3};
    private final int[] descriptions = {R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5};
    private final String[] countries = {"FRANCE", "KOREA", "ENGLAND", "CHINA", "GREECE"};
    private final String[] places = {"The Louvre", "Gwanghwamun", "Tower Bridge", "Temple of Heaven", "Aegeana Sea"};
    private final String[] temperatures = {"8~21°C", "6~19°C", "5~17°C"};
    private final String[] times = {"4.11~11.15    7:00~18:00", "3.15~9.15    8:00~16:00", "8.1~12.15    7:00~18:00"};

    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, 20, new OnCardClickListener());

    private RecyclerView recyclerView;
    private ImageSwitcher mapSwitcher;
    private TextSwitcher countrySwitcher;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;
    private View greenDot;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int left = getResources().getDimensionPixelSize(R.dimen.active_card_left);
        final int width = getResources().getDimensionPixelSize(R.dimen.active_card_width);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setLayoutManager(new CardSliderLayoutManager(left, width));
        recyclerView.addOnScrollListener(new ScrollListener());
        recyclerView.setHasFixedSize(true);

        new CardSnapHelper().attachToRecyclerView(recyclerView);

        countrySwitcher = (TextSwitcher) findViewById(R.id.ts_country);
        countrySwitcher.setFactory(new TextViewFactory(R.style.CountryTextView));
        countrySwitcher.setCurrentText(countries[0]);

        temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView));
        temperatureSwitcher.setCurrentText(temperatures[0]);

        placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView));
        placeSwitcher.setCurrentText(places[0]);

        clockSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
        clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView));
        clockSwitcher.setCurrentText(times[0]);

        descriptionsSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
        descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView));
        descriptionsSwitcher.setCurrentText(getString(descriptions[0]));

        mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
        mapSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mapSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        mapSwitcher.setImageResource(maps[0]);

        mapSwitcher.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapSwitcher.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final int viewLeft = mapSwitcher.getLeft();
                final int viewTop = mapSwitcher.getTop();

                final int border = 100;
                final int xRange = mapSwitcher.getWidth() - border * 2;
                final int yRange = mapSwitcher.getHeight() - border * 2;
                final Random rnd = new Random();

                for (int i = 0, cnt = dotCoords.length; i < cnt; i++) {
                    dotCoords[i][0] = viewLeft + border + rnd.nextInt(xRange);
                    dotCoords[i][1] = viewTop + border + rnd.nextInt(yRange);
                }

                greenDot = findViewById(R.id.green_dot);
                greenDot.setX(dotCoords[0][0]);
                greenDot.setY(dotCoords[0][1]);
            }
        });
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes final int styleId;

        TextViewFactory(@StyleRes int styleId) {
            this.styleId = styleId;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(MainActivity.this);
            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(MainActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            final CardSliderLayoutManager lm = ((CardSliderLayoutManager) recyclerView.getLayoutManager());
            final int pos = lm.getActiveCardPosition();
            if (pos != currentPosition) {
                int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
                int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

                if (pos < currentPosition) {
                    animH[0] = R.anim.slide_in_left;
                    animH[1] = R.anim.slide_out_right;

                    animV[0] = R.anim.slide_in_bottom;
                    animV[1] = R.anim.slide_out_top;
                }

                countrySwitcher.setInAnimation(MainActivity.this, animH[0]);
                countrySwitcher.setOutAnimation(MainActivity.this, animH[1]);
                countrySwitcher.setText(countries[pos % countries.length]);

                temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
                temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
                temperatureSwitcher.setText(temperatures[pos % temperatures.length]);

                placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
                placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
                placeSwitcher.setText(places[pos % places.length]);

                clockSwitcher.setInAnimation(MainActivity.this, animV[0]);
                clockSwitcher.setOutAnimation(MainActivity.this, animV[1]);
                clockSwitcher.setText(times[pos % times.length]);

                descriptionsSwitcher.setText(getString(descriptions[pos % descriptions.length]));

                mapSwitcher.setImageResource(maps[pos % maps.length]);

                ViewCompat.animate(greenDot)
                        .translationX(dotCoords[pos % dotCoords.length][0])
                        .translationY(dotCoords[pos % dotCoords.length][1])
                        .start();

                currentPosition = pos;
            }
        }
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, pics[activeCardPosition % pics.length]);

                if (Build.VERSION.SDK_INT < 21) {
                    startActivity(intent);
                } else {
                    final ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, view, "shared");
                    startActivity(intent, options.toBundle());
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
            }
        }
    }

}
