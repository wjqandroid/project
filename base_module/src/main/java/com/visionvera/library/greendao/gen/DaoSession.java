package com.visionvera.library.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.visionvera.library.greendao.SearchHistoryDBBean;
import com.visionvera.library.greendao.testbean.Student;

import com.visionvera.library.greendao.gen.SearchHistoryDBBeanDao;
import com.visionvera.library.greendao.gen.StudentDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig searchHistoryDBBeanDaoConfig;
    private final DaoConfig studentDaoConfig;

    private final SearchHistoryDBBeanDao searchHistoryDBBeanDao;
    private final StudentDao studentDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        searchHistoryDBBeanDaoConfig = daoConfigMap.get(SearchHistoryDBBeanDao.class).clone();
        searchHistoryDBBeanDaoConfig.initIdentityScope(type);

        studentDaoConfig = daoConfigMap.get(StudentDao.class).clone();
        studentDaoConfig.initIdentityScope(type);

        searchHistoryDBBeanDao = new SearchHistoryDBBeanDao(searchHistoryDBBeanDaoConfig, this);
        studentDao = new StudentDao(studentDaoConfig, this);

        registerDao(SearchHistoryDBBean.class, searchHistoryDBBeanDao);
        registerDao(Student.class, studentDao);
    }
    
    public void clear() {
        searchHistoryDBBeanDaoConfig.clearIdentityScope();
        studentDaoConfig.clearIdentityScope();
    }

    public SearchHistoryDBBeanDao getSearchHistoryDBBeanDao() {
        return searchHistoryDBBeanDao;
    }

    public StudentDao getStudentDao() {
        return studentDao;
    }

}
