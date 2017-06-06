package com.cvter.nynote.Presenter;

import android.os.Handler;
import android.os.Looper;

import com.cvter.nynote.Model.NoteInfo;
import com.cvter.nynote.View.View.IMainView;


import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by cvter on 2017/6/2.
 */

public class MainPresenterImpl implements IMainPresenter{

    private IMainView iMainView;
    private Handler handler;

    private ArrayList<NoteInfo> notes = new ArrayList<>();

    public MainPresenterImpl(IMainView iMainView){
        this.iMainView = iMainView;
        handler = new Handler(Looper.getMainLooper());

    }

    @Override
    public void deleteNote() {

    }

    @Override
    public void getNoteImage(final File file) {

        Observable.just(file)
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {//遍历文件夹
                        return Observable.from(file.listFiles());
                    }
                })
                .filter(new Func1<File, Boolean>() {//过滤图片
                    @Override
                    public Boolean call(File file) {

                        return file.getName().endsWith(".png")||file.getName().endsWith(".jpg")||file.getName().endsWith(".jpeg");
                    }
                })
                .map(new Func1<File, NoteInfo>() {
                    @Override
                    public NoteInfo call(File file) {
                        NoteInfo note = new NoteInfo();
                        note.setNoteName(file.getName());
                        note.setNotePic(file.getPath());
                        return note;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NoteInfo>() {
                    @Override
                    public void onCompleted() {//onNext（）执行完后调用
                        iMainView.onLoadImagesCompleted(notes);
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(NoteInfo note) {
                        notes.add(note);
                    }
                });

    }

}
