/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// 搜索代码
import { environmentList } from '@/api/transfer'
import { themeListByCluster } from '@/api/theme'

export default {
  data() {
    return {
      // 搜索表单参数
      formData: {
        envId: '',
        topicName: '', //主题
        type: 'msgId',
        searchText: ''
      },
      SearchEnv: {
        options: [],
        loading: false
      },
      SearchTheme: {
        options: [],
        loading: false
      },
      SearchType: {
        options: [
          { label: '消息Id', value: 'msgId' },
          { label: 'key', value: 'key' }
        ]
      }
    }
  },
  computed: {
    envId() {
      return this.formData.envId
    }
  },
  watch: {
    // 监听环境值变化，重置主题选项值
    envId() {
      this.formData.topicName = ''
    }
  },
  methods: {
    // 获取环境下拉列表
    async getEnvOptions() {
      this.SearchEnv.loading = true
      const res = await environmentList()
      if (res.result) {
        this.SearchEnv.options = res.result.map(item => {
          return {
            label: item.environmentName,
            value: item.id,
            ...item
          }
        })
        this.SearchEnv.loading = false
      }
    },
    // 获取主题下拉列表
    async getThemeOptions() {
      this.SearchTheme.loading = true
      const params = { envId: this.formData.envId }
      const res = await themeListByCluster(params)
      if (res.result) {
        this.SearchTheme.options = res.result.map(item => {
          return {
            label: item.name,
            value: item.name,
            ...item
          }
        })
      }
      this.SearchTheme.loading = false
    },
    // 查询
    searchHandler() {
      this.$refs.formData.validate(valid => {
        if (!valid) return
        this.loadTabledata(this.formData)
      })
    }
  }
}
