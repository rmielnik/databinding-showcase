package io.rmielnik.toast.calculations;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.rmielnik.toast.R;
import io.rmielnik.toast.calculations.request.AServiceRequest;
import io.rmielnik.toast.calculations.request.BServiceRequest;
import io.rmielnik.toast.calculations.request.Request;
import io.rmielnik.toast.calculations.response.AResponse;
import io.rmielnik.toast.calculations.response.BResponse;
import io.rmielnik.toast.calculations.response.Response;
import io.rmielnik.toast.databinding.ActivityExampleCalculationsBinding;

public class CalculationsActivity extends AppCompatActivity {

    public static final String EXTRA_GREETING = "extra.greeting";

    public static Intent getStartIntent(Context context, String greeting) {
        Intent intent = new Intent(context, CalculationsActivity.class);
        intent.putExtra(EXTRA_GREETING, greeting);
        return intent;
    }

    private CalcService aService = new CalcService();
    private CalcService bService = new CalcService();

    private ActivityExampleCalculationsBinding binding;
    private CalculationsViewModel model = new CalculationsViewModel();
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initModel();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_example_calculations);
        binding.setModel(model);

        final ObservableTransformer<AServiceRequest, AResponse> consumeA =
                stream -> stream
                        .observeOn(Schedulers.io())
                        .flatMap(new Function<AServiceRequest, ObservableSource<AResponse>>() {
                            @Override
                            public ObservableSource<AResponse> apply(@NonNull AServiceRequest aServiceRequest) throws Exception {
                                return Observable.fromCallable(
                                        () -> AResponse.success(aService.randomSleep()))
                                        .startWith(AResponse.inProgress());
                            }
                        });

        final ObservableTransformer<BServiceRequest, BResponse> consumeB =
                stream -> stream
                        .observeOn(Schedulers.io())
                        .flatMap(new Function<BServiceRequest, ObservableSource<BResponse>>() {
                            @Override
                            public ObservableSource<BResponse> apply(@NonNull BServiceRequest bServiceRequest) throws Exception {
                                return Observable.fromCallable(
                                        () -> BResponse.success(bService.randomSleep()))
                                        .startWith(BResponse.inProgress());
                            }
                        });

        final ObservableTransformer<Request, Response> splitAndMerge =
                events -> events.publish(shared -> Observable.merge(
                        shared.ofType(AServiceRequest.class).compose(consumeA),
                        shared.ofType(BServiceRequest.class).compose(consumeB)
                ));

        disposable = RxView.clicks(binding.run)
                .observeOn(Schedulers.computation())
                .flatMap(new Function<Object, ObservableSource<Request>>() {
                    @Override
                    public ObservableSource<Request> apply(@NonNull Object o) throws Exception {
                        return Observable.fromArray(new AServiceRequest(), new BServiceRequest());
                    }
                })
                .compose(splitAndMerge)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(consumeMagicNumbers());
    }

    private void initModel() {
        String greeting = getIntent().getStringExtra(EXTRA_GREETING);
        model.greeting.set(greeting);
    }

    @android.support.annotation.NonNull
    private DisposableObserver<Response> consumeMagicNumbers() {
        return new DisposableObserver<Response>() {
            @Override
            public void onNext(@NonNull Response response) {
                boolean inProgress = response.isProgress();
                int result = response.getResult();

                if (response instanceof AResponse) {
                    model.aResult.set(inProgress ? "In progress" : "A request executed in: " + result + "ms");
                }
                if (response instanceof BResponse) {
                    model.bResult.set(inProgress ? "In progress" : "B request executed in: " + result + "ms");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
