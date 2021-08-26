package com.jcohy.sample.reactive.chapter_01.completion_stage;

import java.util.concurrent.CompletionStage;

import com.jcohy.sample.reactive.chapter_01.commons.Input;
import com.jcohy.sample.reactive.chapter_01.commons.Output;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/26:15:51
 * @since 1.0.0
 */
public interface ShoppingCardService {

    CompletionStage<Output> calculate(Input input);
}
