package com.marf.colonization.decompile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.marf.colonization.reader.GameDataSection;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class Address extends GameDataSection implements Comparable<Address> {
    private final int value;

    @JsonCreator()
    public Address(String value) {
        this.value = Integer.parseInt(value, 16);
    }

    public Address() {
        this(0);
    }
    public Address(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Address o) {
        return value - o.value;
    }

    public Address sub(Address other) {
        return new Address(value - other.value);
    }

    public Address sub(int other) {
        return new Address(value - other);
    }


    public Address add(Address other) {
        return new Address(value + other.value);
    }

    public Address add(int other) {
        return new Address(value + other);
    }

    @Override
    public String toString() {
        String hexString = Integer.toHexString(value);
        return "0".repeat(Math.max(0, 4 - hexString.length()))+hexString;
    }
}
