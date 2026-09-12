package net.smileycorp.mounts.client.particle;

/**
 * This enum just simplifies writing particles.
* */
public enum MountsParticle
{
    SPEAR_PIERCE;

    public int getId() { return this.ordinal(); }

    public static MountsParticle fromId(int id) { return values()[id]; }
}