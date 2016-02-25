package com.marco.zombiescape;

/**
 * Created by marco on 18/02/16.
 */
public interface Hitable {

    interface HitableListener{
        void hitted(Hitable hitable);
    }

    void beginHit();
    void endHit();
    int getLife();
    void addHitListener(HitableListener hitableListener);
}
