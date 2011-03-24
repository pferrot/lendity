package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.EvaluationDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Evaluation;

public interface EvaluationDao {
	
	Long createEvaluation(Evaluation pEvaluation);
	
	Evaluation findEvaluation(Long pEvaluationtId);
	
	ListWithRowCount findEvaluations(EvaluationDaoQueryBean pEvaluationDaoQueryBean);
	List<Evaluation> findEvaluationsList(EvaluationDaoQueryBean pEvaluationDaoQueryBean);
	long countEvaluations(EvaluationDaoQueryBean pEvaluationDaoQueryBean);
			
	void updateEvaluation(Evaluation pEvaluation);
	
	void deleteEvaluation(Evaluation pEvaluation);
}
