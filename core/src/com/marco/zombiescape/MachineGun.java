package com.marco.zombiescape;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by marco on 27/02/16.
 */
public class MachineGun extends Weapon {
    protected MachineGun(Body body) {
        super(body);
    }

    @Override
    protected int shootBeforeCooldDown() {
        return 5;
    }

    @Override
    protected float shootDelay() {
        return 0.1f;
    }

    @Override
    protected float shootCoolDown() {
        return 1;
    }
}
