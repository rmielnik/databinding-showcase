package io.rmielnik.toast.calculations;

import android.databinding.ObservableField;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.rmielnik.toast.calculations.request.AServiceRequest;
import io.rmielnik.toast.calculations.request.BServiceRequest;
import io.rmielnik.toast.calculations.request.Request;
import io.rmielnik.toast.calculations.response.AResponse;
import io.rmielnik.toast.calculations.response.BResponse;
import io.rmielnik.toast.calculations.response.Response;

public class CalculationsViewModel {

    public final ObservableField<String> aResult = new ObservableField<>("Idle");
    public final ObservableField<String> bResult = new ObservableField<>("Idle");
    public final ObservableField<String> greeting = new ObservableField<>();

    private ObservableTransformer<Request, Response> computationsHandler;
    private Disposable ongoingRequest;

    private CalcService aService = new CalcService();
    private CalcService bService = new CalcService();

    public CalculationsViewModel() {
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

        computationsHandler =
                events -> events.publish(shared -> Observable.merge(
                        shared.ofType(AServiceRequest.class).compose(consumeA),
                        shared.ofType(BServiceRequest.class).compose(consumeB)
                ));
    }

    public void calculate() {
        if (ongoingRequest != null && !ongoingRequest.isDisposed()) {
            ongoingRequest.dispose();
        }
        ongoingRequest = Observable.fromArray(new AServiceRequest(), new BServiceRequest())
                .observeOn(Schedulers.computation())
                .compose(computationsHandler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(consumeMagicNumbers());
    }

    @android.support.annotation.NonNull
    private DisposableObserver<Response> consumeMagicNumbers() {
        return new DisposableObserver<Response>() {
            @Override
            public void onNext(@NonNull Response response) {
                boolean inProgress = response.isProgress();
                int result = response.getResult();

                if (response instanceof AResponse) {
                    aResult.set(inProgress ? "In progress" : "A request executed in: " + result + "ms");
                }
                if (response instanceof BResponse) {
                    bResult.set(inProgress ? "In progress" : "B request executed in: " + result + "ms");
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
}
