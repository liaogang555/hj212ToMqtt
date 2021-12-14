package com.szewec.netty.hj212.format.hbt212.core.validator;

import com.szewec.netty.hj212.format.hbt212.model.DataFlag;
import com.szewec.netty.hj212.format.hbt212.model.verify.DataElement;
import com.szewec.netty.hj212.format.hbt212.model.verify.T212Map;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by xiaoyao9184 on 2018/1/10.
 */
@SuppressWarnings("rawtypes")
public class T212MapValueRangeValidator
        implements ConstraintValidator<ValueRange, T212Map> {

    @SuppressWarnings("unused")
	private ValueRange fieldMissing;


    @Override
    public void initialize(ValueRange fieldMissing) {
        this.fieldMissing = fieldMissing;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean isValid(T212Map value, ConstraintValidatorContext constraintValidatorContext) {
        Map<String, String> result = value;

        Stream<DataElement> stream = Stream.of(DataElement.values())
                .filter(DataElement::isRequired);
        if(result.containsKey(DataElement.Flag.name())){
            String f = result.get(DataElement.Flag.name());
            int flag = Integer.valueOf(f);
            if(DataFlag.D.isMarked(flag)){
                stream = Stream.concat(stream,Stream.of(DataElement.PNO, DataElement.PNUM));
            }
        }

        Optional<DataElement> missing = stream
                .filter(e -> !result.containsKey(e.name()))
                .findFirst();
        if(missing.isPresent()){
//            T212FormatException.field_is_missing(PacketElement.DATA,missing.get().name());
            return false;
        }
        return true;
    }

}
