package observers;

public interface LobbyObservable {
        void register(LobbyObserver observer);
        void notifyAllObservers();
}
