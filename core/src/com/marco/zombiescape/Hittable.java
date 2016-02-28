package com.marco.zombiescape;

/**
 * Created by marco on 18/02/16.
 */
public interface Hittable {

    interface HittableListener {
        void hitted(Hittable hittable);
    }

    void beginHit(float damage);
    void endHit();
    float getLife();
    float getMaxLife();
    void addHitListener(HittableListener hittableListener);
}
