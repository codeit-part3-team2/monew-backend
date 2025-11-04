package com.monew.monew_api.interest;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.test.util.ReflectionTestUtils;

public class TestInterestForm {

  // interestId Long 생성기
  private static Long generatedId(){
    return new AtomicLong(1).getAndIncrement();
  }

  public static Interest create(String name, List<String> keywords) {
    Interest interest = Interest.create(name);

    for (String keyword : keywords) {
      interest.addKeyword(new Keyword(keyword));
    }
    ReflectionTestUtils.setField(interest, "id", generatedId());
    return interest;
  }
}
