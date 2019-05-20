/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.converters

import com.sf.shared.dto.MatrixRow
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope

/**
 *
 * @author sf
 */
@SpringComponent
//@UIScope
class FieldsGenerator {
    MatrixRow toMatrixRow(String[] row) {
        for (j in 0..row.length) {
            MatrixRow.metaClass['getJ' + j] << {-> row[j]};
        }
        return new MatrixRow();
    }
}

