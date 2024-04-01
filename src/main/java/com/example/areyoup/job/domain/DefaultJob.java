package com.example.areyoup.job.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("D")
@NoArgsConstructor
@Getter
@SuperBuilder
public class DefaultJob extends Job{
    public static DefaultJob root(){
        return new DefaultJob();
    }
}
