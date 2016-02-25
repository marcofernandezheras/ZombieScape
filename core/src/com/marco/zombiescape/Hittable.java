package com.marco.zombiescape;

/**
 * Created by marco on 18/02/16.
 */
public interface Hittable {

    interface HittableListener {
        void hitted(Hittable hittable);
    }

    void beginHit();
    void endHit();
    int getLife();
    void addHitListener(HittableListener hittableListener);
}
