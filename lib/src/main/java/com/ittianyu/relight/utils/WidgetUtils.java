package com.ittianyu.relight.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ittianyu.relight.view.AndroidRender;
import com.ittianyu.relight.widget.Widget;
import com.ittianyu.relight.widget.native_.LifecycleAndroidWidget;

import java.lang.reflect.Constructor;

public class WidgetUtils {

    public static <T extends View> LifecycleAndroidWidget<T> createAndroidWidget(Context context, AndroidRender<T> androidRender, Lifecycle lifecycle) {
        return new LifecycleAndroidWidget<T>(context, lifecycle) {
            @Override
            public T createView(Context context) {
                return androidRender.createView(context);
            }

            @Override
            public void initView(T view) {
                androidRender.initView(view);
            }

            @Override
            public void updateView(T view) {
                androidRender.updateView(view);
            }

            @Override
            public void bindEvent(T view) {
                androidRender.bindEvent(view);
            }

            @Override
            public void initData() {
                androidRender.initData();
            }
        };
    }

    public static <T extends View> Widget<T> create(Context context, T view) {
        return new Widget<T>(context) {
            @Override
            public T render() {
                return view;
            }
        };
    }

    public static <T extends View> T render(Fragment fragment, Class<? extends Widget<T>> widgetClass, Object... params) {
        return render(fragment.getActivity(), fragment, widgetClass, params);
    }

    public static <T extends View> T render(AppCompatActivity activity, Class<? extends Widget<T>> widgetClass, Object... params) {
        return render(activity, activity, widgetClass, params);
    }

    public static <T extends View> T render(Context context, LifecycleOwner lifecycleOwner, Class<? extends Widget<T>> widgetClass, Object... params) {
        Class[] clazzs = new Class[params.length + 2];
        clazzs[0] = Context.class;
        clazzs[1] = Lifecycle.class;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            clazzs[i + 2] = param.getClass();
        }

        Object[] ps = new Object[params.length + 2];
        ps[0] = context;
        ps[1] = lifecycleOwner.getLifecycle();
        for (int i = 0; i < params.length; i++) {
            ps[i + 2] = params[i];
        }
        try {
            Constructor<? extends Widget<T>> constructor = widgetClass.getConstructor(clazzs);
            Widget<T> widget = constructor.newInstance(ps);
            return widget.render();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
