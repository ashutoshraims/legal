// Copyright © Amazon Web Services
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.legal.aws.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.opengroup.osdu.core.aws.dynamodb.DynamoDBQueryHelper;
import org.opengroup.osdu.core.aws.dynamodb.QueryPageResult;
import org.opengroup.osdu.legal.aws.tags.dataaccess.LegalDoc;
import org.opengroup.osdu.legal.aws.tags.dataaccess.LegalTagRepositoryImpl;
import org.opengroup.osdu.core.common.model.legal.ListLegalTagArgs;
import org.opengroup.osdu.core.common.model.legal.LegalTag;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class LegalTagRepositoryImplTest {

    @InjectMocks
    private LegalTagRepositoryImpl repo = new LegalTagRepositoryImpl();

    @Mock
    DynamoDBQueryHelper queryHelper;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testCreate(){
        // arrange
        LegalTag tag = new LegalTag();
        tag.setId(-100L);

        LegalDoc ld = new LegalDoc();
        ld.setId(String.valueOf(-100L));
        ArgumentCaptor<LegalDoc> ldArg = ArgumentCaptor.forClass(LegalDoc.class);

        // act
        repo.create(tag);

        // assert
        Mockito.verify(queryHelper, Mockito.times(1)).save(ldArg.capture());
    }

    @Test
    public void testGet(){
        // arrange
        long[] ids = new long[]{
            1L, 2L, 3L
        };

        Mockito.when(queryHelper.loadByPrimaryKey(Mockito.any(), Mockito.anyString())).thenReturn(null);

        // act
        repo.get(ids);

        // assert
        Mockito.verify(queryHelper, Mockito.times(1)).loadByPrimaryKey(Mockito.any(), Mockito.eq("1"));
        Mockito.verify(queryHelper, Mockito.times(1)).loadByPrimaryKey(Mockito.any(), Mockito.eq("2"));
        Mockito.verify(queryHelper, Mockito.times(1)).loadByPrimaryKey(Mockito.any(), Mockito.eq("3"));
    }

    @Test
    public void testDelete(){
        // arrange
        LegalTag tag = new LegalTag();
        tag.setId(-100L);

        LegalDoc ld = new LegalDoc();
        ld.setId(String.valueOf(-100L));

        // act
        repo.delete(tag);

        // assert
        Mockito.verify(queryHelper, Mockito.times(1)).deleteByPrimaryKey(Mockito.any(), Mockito.eq("-100"));
    }

    @Test
    public void testUpdate(){
        // arrange
        LegalTag tag = new LegalTag();
        tag.setId(-100L);

        LegalDoc ld = new LegalDoc();
        ld.setId(String.valueOf(-100L));
        ArgumentCaptor<LegalDoc> ldArg = ArgumentCaptor.forClass(LegalDoc.class);

        // act
        repo.update(tag);

        // assert
        Mockito.verify(queryHelper, Mockito.times(1)).save(ldArg.capture());
    }

    @Test
    public void testListIsValidTrue() {
        // arrange
        ListLegalTagArgs args = new ListLegalTagArgs();
        args.setIsValid(true);
        args.setCursor("{{\"title\"={\"S\":\"Monster on the Campus\",}},{\"title\":{\"S\":\"+1\",}},}}}");
        args.setLimit(100);
        LegalDoc ld = new LegalDoc();
        ld.setId(String.valueOf(-100L));
        ld.setName("test-name");
        ld.setDescription("test-desc");
        ld.setIsValid(true);
        List<LegalDoc> listLd = new ArrayList<>();
        listLd.add(ld);
        QueryPageResult<LegalDoc> page = new QueryPageResult<LegalDoc>(null, listLd);

        try {
            Mockito.when(queryHelper.scanPage(Mockito.eq(LegalDoc.class), Mockito.eq(100),Mockito.any()))
                    .thenReturn(page);
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Unexpected exception");
        }

        // act
        ArrayList<LegalTag> tags = (ArrayList)repo.list(args);
        Long tagId = tags.get(0).getId();

        // assert
        Assert.assertEquals((long)tagId, -100L);
    }

    @Test
    public void testListIsValidFalse(){
        // arrange
        ListLegalTagArgs args = new ListLegalTagArgs();
        args.setIsValid(true);
        args.setCursor("{{\"title\"={\"S\":\"Monster on the Campus\",}},{\"title\":{\"S\":\"+1\",}},}}}");
        args.setLimit(100);
        LegalDoc ld = new LegalDoc();
        ld.setId(String.valueOf(-100L));
        ld.setName("test-name");
        ld.setDescription("test-desc");
        ld.setIsValid(false);
        List<LegalDoc> listLd = new ArrayList<>();
        listLd.add(ld);
        QueryPageResult<LegalDoc> page = new QueryPageResult<LegalDoc>(null, listLd);

        try {
            Mockito.when(queryHelper.scanPage(Mockito.eq(LegalDoc.class), Mockito.eq(100),Mockito.any()))
                    .thenReturn(page);
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Unexpected exception");
        }

        // act
        ArrayList<LegalTag> tags = (ArrayList)repo.list(args);

        // assert
        Assert.assertEquals(0, tags.size());
    }

    @Test
    public void testListReturnsNull(){
        // arrange
        ListLegalTagArgs args = new ListLegalTagArgs();
        args.setIsValid(true);
        args.setCursor("{{\"title\"={\"S\":\"Monster on the Campus\",}},{\"title\":{\"S\":\"+1\",}},}}}");
        args.setLimit(100);

        try {
            Mockito.when(queryHelper.scanPage(Mockito.eq(LegalDoc.class), Mockito.eq(100),Mockito.any()))
                    .thenReturn(null);
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Unexpected exception");
        }

        // act
        ArrayList<LegalTag> tags = (ArrayList)repo.list(args);

        // assert
        Assert.assertEquals(0, tags.size());
    }
}
